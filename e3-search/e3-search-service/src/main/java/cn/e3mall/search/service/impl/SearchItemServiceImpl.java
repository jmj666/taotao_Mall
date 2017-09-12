package cn.e3mall.search.service.impl;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//查询所有商品数据，导入solr索引库
//添加的商品根据其ID查询到其商品信息后，将其加入到solr索引库
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.SearchItemMapper;
import cn.e3mall.search.service.SearchItemService;
@Service
public class SearchItemServiceImpl implements SearchItemService{

	@Autowired
	private SearchItemMapper itemMapper;
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public E3Result importItems() {
		try {
		//查询商品列表
		List<SearchItem> itemList=itemMapper.getItemList();
		//循环把商品数据导入索引库
		for(SearchItem item:itemList)
		{
			//创建文档对象
			SolrInputDocument document=new SolrInputDocument();
			//向文档对象中添加域
			document.addField("id", item.getId());
			document.addField("item_title", item.getTitle());
			document.addField("item_sell_point", item.getSell_point());
			document.addField("item_price", item.getPrice());
			document.addField("item_image", item.getImage());
			document.addField("item_category_name", item.getCategory_name());
			//文档添加到索引库
			solrServer.add(document);
		}
		//提交
		solrServer.commit();
		//返回E3Result
		return E3Result.ok();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return E3Result.build(500, "商品导入失败");
		} 
	}


}
