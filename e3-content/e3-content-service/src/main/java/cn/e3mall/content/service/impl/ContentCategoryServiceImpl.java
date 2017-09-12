package cn.e3mall.content.service.impl;
//展示内容分类、添加节点、重命名

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//取查询参数parentId
		//根据parentId查询tb_content_category，查询子节点列表
		TbContentCategoryExample example=new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria =example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		//得到List<TbContentCategory>
		List<TbContentCategory> list=contentCategoryMapper.selectByExample(example);
		//列表转换为List<EasyUITreeNode>
		List<EasyUITreeNode> resultList=new ArrayList<>();
		for(TbContentCategory tbContentCategory:list)
		{
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			node.setText(tbContentCategory.getName());
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public E3Result addContentCategory(long parentId, String name) {
		//向tb_content_category表中插入数据
		//创建Tb_content_category对象
		TbContentCategory tbContentCategory=new TbContentCategory();
		//补全Tb_content_category对象的属性
		tbContentCategory.setIsParent(false);
		tbContentCategory.setName(name);
		tbContentCategory.setParentId(parentId);
		//排列序号，同级展现次序，>0
		tbContentCategory.setSortOrder(1);
		//状态，1（正常），2（删除）
		tbContentCategory.setStatus(1);
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setUpdated(new Date());
		//向tb_content_category表中插入数据
		contentCategoryMapper.insert(tbContentCategory);
		//判断父节点的isParent是否为true，需改为true
		TbContentCategory parentNode=contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parentNode.getIsParent())
		{
			parentNode.setIsParent(true);
			//更新节点
			contentCategoryMapper.updateByPrimaryKey(parentNode);
		}
		//返回主键
		//返回E3Result
		return E3Result.ok(tbContentCategory);
	}

	@Override
	public E3Result resumeContentCategory(long id, String name) {
		TbContentCategoryExample example=new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria =example.createCriteria();
		criteria.andIdEqualTo(id);
		List<TbContentCategory> list=contentCategoryMapper.selectByExample(example);
		Iterator<TbContentCategory> it=list.iterator();
		while(it.hasNext())
		{
			TbContentCategory tbContentCategory=it.next();
			tbContentCategory.setName(name);
		}
		return E3Result.ok();
		
	}

}
