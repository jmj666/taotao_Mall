package cn.e3mall.search.dao;
//搜索服务Dao层

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

@Repository
public class SearchDao {
	
	@Autowired
	private SolrServer solrServer;
	
	public SearchResult search(SolrQuery query)throws Exception
	{
		//根据查询对象查询索引库
		QueryResponse queryResponse=solrServer.query(query);
		//取查询结果总记录数
		SolrDocumentList solrDocumentList=queryResponse.getResults();
		long numFound=solrDocumentList.getNumFound();
		//创建返回结果对象
		SearchResult searchResult=new SearchResult();
		searchResult.setRecordCount((int)numFound);
		//创建商品列表对象
		List<SearchItem> itemList=new ArrayList<>();
		//取商品列表
		//取高亮后结果
		Map<String, Map<String,List<String>>> hightLighting=queryResponse.getHighlighting();
		for(SolrDocument solrDocument:solrDocumentList)
		{
			//取商品信息
			SearchItem searchItem=new SearchItem();
			searchItem.setCategory_name((String)solrDocument.get("item_category_name"));
			searchItem.setId((String) solrDocument.get("id"));
			searchItem.setImage((String) solrDocument.get("item_image"));
			searchItem.setPrice((long) solrDocument.get("item_price"));
			searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
			//取高亮结果
			List<String> list=hightLighting.get(solrDocument.get("id")).get("item_title");
			String itemTitle="";
			if(list!=null&&list.size()>0)
			{
				itemTitle=list.get(0);//取keyword的第一条记录item_title
			}
			else
			{
				itemTitle=(String) solrDocument.get("item_title");
			}
			searchItem.setTitle(itemTitle);
			itemList.add(searchItem);
		}
		searchResult.setitemList(itemList);
		return searchResult;
	}
}
