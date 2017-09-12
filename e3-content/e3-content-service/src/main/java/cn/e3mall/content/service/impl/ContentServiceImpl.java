package cn.e3mall.content.service.impl;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//内容列表查询,增加内容,首页展示
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {
	
	@Autowired
	private TbContentMapper tbContentMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	@Override
	public EasyUIDataGridResult getContentList(long catgoryid, int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbContentExample example=new TbContentExample();
		Criteria criteria=example.createCriteria();
		criteria.andCategoryIdEqualTo(catgoryid);
		List<TbContent> list=tbContentMapper.selectByExample(example);
		//取分页信息
		PageInfo<TbContent> info=new PageInfo<>(list);
		//创建返回结果对象
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		long total=info.getTotal();
		result.setTotal(total);
		result.setRows(list);
		return result;
	}

	@Override
	public E3Result addContent(TbContent tbContent) {
		//补全Tb_content对象的属性
		tbContent.setCreated(new Date());
		tbContent.setUpdated(new Date());
		//Tb_content表中插入数据
		tbContentMapper.insert(tbContent);
		//缓存同步
		jedisClient.hdel(CONTENT_LIST, tbContent.getCategoryId().toString());
		return E3Result.ok();
	}
	
	
	/**
	 * 根据内容分类id查询内容列表
	 * <p>Title: getContentListByCid</p>
	 * <p>Description: </p>
	 * @param cid
	 * @return
	 * @see cn.e3mall.content.service.ContentService#getContentListByCid(long)
	 */
	@Override
	public List<TbContent> getContentListByCid(long cid) {
		//查询缓存,缓存中存在直接响应
		try{
			String json=jedisClient.hget(CONTENT_LIST,cid+"");
			if(StringUtils.isNotBlank(json))
			{
				List<TbContent> list=JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//缓存中没有查询数据库
		TbContentExample example=new TbContentExample();
		Criteria criteria=example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		//信息全都展示出来
		List<TbContent> list=tbContentMapper.selectByExampleWithBLOBs(example);
		//将在数据库中查询出的结果加入缓存中
		try{
			jedisClient.hset(CONTENT_LIST,cid+"", JsonUtils.objectToJson(list));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
