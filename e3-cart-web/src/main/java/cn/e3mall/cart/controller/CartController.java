package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;


@Controller
public class CartController {

	@Value("${TT_CART}")
	private String TT_CART;
	//设置cookie有效期7天
	@Value("${CART_EXPIRE}")
	private Integer CART_EXPIRE;
	@Autowired
	private ItemService itemService;
	@Autowired
	private CartService cartService;
	
	/**
	 * @author public
	 * <p>Title: CartController </p>
	 * <p>Description: 商品添加到购物车</p>
	 * <p>Company: e3-cart-web </p>
	 * @version 
	 */
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId,Integer num,
			HttpServletRequest request,HttpServletResponse response)
	{
		//判断用户是否为登录状态
		Object object = request.getAttribute("user");
		//如果登录直接把购物车信息添加到服务端
		if (object != null) {
			TbUser user = (TbUser) object;
			//取用户id
			Long userId = user.getId();
			//添加到服务端
			E3Result e3Result = cartService.addCart(userId, itemId, num);
			return "cartSuccess";
		}
		//如果未登录保存到cookie中
		//从cookie中查询商品列表
		List<TbItem> cartList=getCartList(request);
		//判断商品在列表中是否存在
		boolean flag=false;
		for(TbItem item:cartList)
		{
			//"=="比较基本数据类型的值
			if(item.getId()==itemId.longValue())
			{
				//如果存在，增加商品数量num
				item.setNum(item.getNum()+num);
				flag=true;
				//查找到跳出循环
				break;
			}
		}
		//如果商品在列表中不存在，则添加其到购物车列表
		if(!flag)
		{
			TbItem tbItem=itemService.getItemById(itemId); 
			//取一张图片
			String image = tbItem.getImage();
			if(StringUtils.isNoneBlank(image))
			{
				String[] images = image.split(",");
				tbItem.setImage(images[0]);
			}
			//设置购买数量
			tbItem.setNum(num);
			//商品添加到购物车列表
			cartList.add(tbItem);
		}
		//购物车列表再写入cookie
		CookieUtils.setCookie(request, response, TT_CART, JsonUtils.objectToJson(cartList),
				CART_EXPIRE, true);
		return "cartSuccess";
	}
	
	/**
	 * 从cookie中取购物车列表
	 * <p>Title: getCartList</p>
	 * <p>Description: </p>
	 * @param request
	 * @return
	 */
	private List<TbItem> getCartList(HttpServletRequest request)
	{
		//取购物车列表
		String json=CookieUtils.getCookieValue(request, TT_CART, true);
		//判断json是否为空
		if(StringUtils.isNotBlank(json))
		{
			//把json转换为商品列表返回
			List<TbItem> list =JsonUtils.jsonToList(json, TbItem.class);
			return list;
		}
		return new ArrayList<>();
	}
	
	
	/**
	 * 展示购物车列表
	 * <p>Title: getCartList</p>
	 * <p>Description: </p>
	 * @param request
	 * @return
	 */
	
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request, HttpServletResponse response) {
		//从cookie中取购物车列表
		List<TbItem> cartList = getCartList(request);
		//判断用户是否登录
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object; 
			//用户已经登录
			System.out.println("用户已经登录，用户名为：" + user.getUsername());
			//判断给我吃列表是否为空
			if (!cartList.isEmpty()) {
				//合并购物车
				cartService.mergeCart(user.getId(), cartList);
				//删除cookie中的购物车
				CookieUtils.setCookie(request, response, TT_CART, "");
			}
			//从服务端取购物车列表
			List<TbItem> list = cartService.getList(user.getId());
			request.setAttribute("cartList", list);
			return "cart";
		} else {
			System.out.println("用户未登录");
		}
		//传递给页面
		request.setAttribute("cartList", cartList);
		return "cart";
	}
	
	
	/**
	 * 增加/减少购物车商品数量
	 * <p>Title: getCartList</p>
	 * <p>Description: </p>
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	public E3Result updateNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response)
	{
		//判断是否为登录状态
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object;
			//更新服务端的购物车
			cartService.updateCartItemNum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		//从cookie中取商品列表
		List<TbItem> cartList = getCartList(request);
		//遍历商品列表找到对应商品
		for(TbItem tbItem:cartList)
		{
			if(tbItem.getId()==itemId.longValue())
			{
				//更新商品数量
				tbItem.setNum(num);
			}
		}
		//商品列表写入cookie
		CookieUtils.setCookie(request, response, TT_CART, JsonUtils.objectToJson(cartList), CART_EXPIRE, true);
		//返回E3Result
		return E3Result.ok();
	}
	
	/**
	 * 删除指定购物车商品
	 * <p>Title: getCartList</p>
	 * <p>Description: </p>
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,HttpServletRequest request,HttpServletResponse response)
	{
		//判断是否为登录状态
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object;
			//更新服务端的购物车
			cartService.deleteCartItem(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		//从cookie中取商品列表
		List<TbItem> cartList = getCartList(request);
		//遍历商品列表找到对应商品
		for(TbItem tbItem:cartList)
		{
			if(tbItem.getId()==itemId.longValue())
			{
				cartList.remove(tbItem);
				break;
			}
		}
		//商品列表写入cookie
		CookieUtils.setCookie(request, response, TT_CART, JsonUtils.objectToJson(cartList), CART_EXPIRE, true);
		//返回逻辑视图
		return "redirect:/cart/cart.html";
	}
}
