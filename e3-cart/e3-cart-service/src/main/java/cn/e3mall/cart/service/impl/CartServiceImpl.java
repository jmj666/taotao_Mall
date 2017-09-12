package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;




public class CartServiceImpl implements CartService{
	
	
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbItemMapper itemMapper;
	@Value("${CART_REDIS_KEY}")
	private String CART_REDIS_KEY;
	/**
	 * @author public
	 * <p>Title: CartServiceImpl </p>
	 * <p>Description: 登录状态下添加商品到购物车，写入redis</p>
	 * <p>Company: e3-cart-service </p>
	 * @version 
	 */
	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		//判断购物车中是否有此商品
		Boolean flag=jedisClient.hexists(CART_REDIS_KEY+":"+userId, itemId+"");
		//如果有，数量相加
		if(flag)
		{
			//从hash表中取商品数据
			String json = jedisClient.hget(CART_REDIS_KEY+":"+userId, itemId+"");
			//转换成java对象
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			//数量相加
			tbItem.setNum(tbItem.getNum()+num);
			//写入hash
			jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
			//返回添加成功
			E3Result.ok();
		}
		//如果没有，根据商品id查询商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		//设置商品数量
		item.setNum(num);
		String image=item.getImage();
		//取一张照片
		if(StringUtils.isNotBlank(image))
		{
			item.setImage(image.split(",")[0]);
		}
		//写入hash
		jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(item));
		return E3Result.ok();
	}
	
	/**
	 * <p>Title: mergeCart</p>
	 * <p>Description: 合并购物车</p>
	 * @param userId
	 * @param itemList
	 * @return E3Result
	 */
	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		//遍历商品列表
		for (TbItem tbItem : itemList) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		return E3Result.ok();
	}

	/**
	 * <p>Title: getList</p>
	 * <p>Description:从服务端取购物车列表</p>
	 * @param userId
	 * @return List<TbItem>
	 */
	@Override
	public List<TbItem> getList(long userId) {
		//从redis中根据用户id查询商品列表
		List<String> strList = jedisClient.hvals(CART_REDIS_KEY + ":" + userId);
		List<TbItem> resultList = new ArrayList<>();
		//把json列表转换成TbItem列表
		for (String string : strList) {
			TbItem tbItem = JsonUtils.jsonToPojo(string, TbItem.class);
			//添加到列表
			resultList.add(tbItem);
		}
		return resultList;
	}
 
	/**
	 * <p>Title: updateCartItemNum</p>
	 * <p>Description:更新购物车中商品数量</p>
	 * @param userId
	 * @return E3Result
	 */
    @Override
	public E3Result updateCartItemNum(long userId, long itemId, int num) {
		//从hash中取商品信息
		String json = jedisClient.hget(CART_REDIS_KEY + ":" + userId, itemId + "");
		//转换成java对象
		TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
		//更新数量
		tbItem.setNum(num);
		//写入hash
		jedisClient.hset(CART_REDIS_KEY + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}
    
    /**
	 * <p>Title: deleteCartItem</p>
	 * <p>Description:删除购物车中指定商品</p>
	 * @param userId
	 * @return E3Result
	 */
    @Override
	public E3Result deleteCartItem(long userId, long itemId) {
		// 根据商品id删除hash中对应的商品数据。
		jedisClient.hdel(CART_REDIS_KEY + ":" + userId, itemId + "");
		return E3Result.ok();
	}
    
    /**
   	 * <p>Title: deleteCartItem</p>
   	 * <p>Description:删除购物车中指定商品</p>
   	 * @param userId
   	 * @return E3Result
   	 */
    @Override
	public E3Result clearCartItem(long userId) {
		//删除购物车信息
		jedisClient.del(CART_REDIS_KEY + ":" + userId);
		return E3Result.ok();
	}
    
    
}
