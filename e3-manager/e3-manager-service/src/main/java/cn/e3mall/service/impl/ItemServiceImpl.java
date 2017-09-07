package cn.e3mall.service.impl;
import java.util.Date;
//查询商品信息
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;
@Service
public class ItemServiceImpl implements ItemService{
	
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination topicDestination;
	@Autowired
	private JedisClient jedisClient;
	

	@Value("${REDIS_ITEM_PRE}")
	private String REDIS_ITEM_PRE;
	@Value("${ITEM_CACHE_EXPIRE}")
	private Integer ITEM_CACHE_EXPIRE;
	
	public TbItem getItemById(long itemId)
	{
			//查询缓存
				try {
					String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":BASE");
					if(StringUtils.isNotBlank(json)) {
						TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
						return tbItem;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				//缓存中没有，查询数据库
				//TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
				TbItemExample example = new TbItemExample();
				Criteria criteria = example.createCriteria();
				
				criteria.andIdEqualTo(itemId);
				
				List<TbItem> list = itemMapper.selectByExample(example);
				if (list != null && list.size() > 0) {
					//把结果添加到缓存
					try {
						jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(list.get(0)));
						//设置过期时间
						jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":BASE", ITEM_CACHE_EXPIRE);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return list.get(0);	
				}
				return null;
	}
	
	public EasyUIDataGridResult getItemList(int page,int rows)
	{
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example=new TbItemExample();
		List<TbItem> list=itemMapper.selectByExample(example);
		//取分页信息
		PageInfo<TbItem> pageInfo=new PageInfo<>(list);
		//创建返回结果对象
		EasyUIDataGridResult result =new EasyUIDataGridResult();
		long total=pageInfo.getTotal();
		result.setTotal(total);
		result.setRows(list);
		return result;
	}

	@Override
	public E3Result addItem(TbItem tbItem, String desc) {
		//生成商品ID
		final long itemId=IDUtils.genItemId();
		//添加TbItem对象的属性
		tbItem.setId(itemId);
		//商品状态，1正常，2下架，3删除
		tbItem.setStatus((byte) 1);
		Date date=new Date();
		tbItem.setCreated(date);
		tbItem.setUpdated(date);
		
		//商品表中插入数据
		itemMapper.insert(tbItem);
		//创建TbItemDesc对象
		TbItemDesc itemDesc=new TbItemDesc();
		//添加TbItemDesc对象的属性
		itemDesc.setItemId(itemId);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		itemDesc.setItemDesc(desc);
		//商品描述表中插入数据
		itemDescMapper.insert(itemDesc);
		//生产者发送一个添加商品消息,包含其id
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage=session.createTextMessage(itemId+"");
				return textMessage;
			}
		});
		//返回对象
		return E3Result.ok();
	}

	@Override
	public TbItemDesc getItemDescById(long itemId) {
		//查询缓存
		try {
			String json = jedisClient.get(REDIS_ITEM_PRE + ":" + itemId + ":DESC");
			if(StringUtils.isNotBlank(json)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc=itemDescMapper.selectByPrimaryKey(itemId);
		//把结果添加到缓存
		try {
			jedisClient.set(REDIS_ITEM_PRE + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
			//设置过期时间
			jedisClient.expire(REDIS_ITEM_PRE + ":" + itemId + ":DESC", ITEM_CACHE_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
}
