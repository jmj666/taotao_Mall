package cn.e3mall.search.message;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.SearchItemMapper;

/**
 * @author public
 * <p>Title: ItemAddMessageListener </p>
 * <p>Description: 监听商品添加消息，接收消息后，将对应的商品信息同步到索引库</p>
 * <p>Company: e3-search-service </p>
 * @version 
 */
public class ItemAddMessageListener implements MessageListener{
	
	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer;
	
	@Override
	public void onMessage(Message message) {
		try{
			TextMessage textMessage=null;
			long itemId=0;
			//取商品id
			if(message instanceof TextMessage)
			{
				textMessage=(TextMessage)message;
				itemId=Long.parseLong(textMessage.getText());
			}
			//等待事务提交
			Thread.sleep(1000);
			
			SearchItem searchItem=searchItemMapper.getItemById(itemId);
			//创建SolrInputDocument对象
			SolrInputDocument document=new SolrInputDocument();
			//使用SolrServer对象写入索引库
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_category_name", searchItem.getCategory_name());
			//向索引库添加文档
			solrServer.add(document);
			solrServer.commit();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
		

}
