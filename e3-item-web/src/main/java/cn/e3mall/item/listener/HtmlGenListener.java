package cn.e3mall.item.listener;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author public
 * <p>Title: HtmlGenListener </p>
 * <p>Description:监听添加商品消息，利用freemarker生成商品详情静态页面 </p>
 * <p>Company: e3-item-web </p>
 * @version 
 */
public class HtmlGenListener implements MessageListener{
	
	@Autowired
	private ItemService itemService;
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Value("${HTML_GEN_PATH}")
	private String HTML_GEN_PATH;//静态页面输出地址
	@Override
	public void onMessage(Message message) {
	  try {
			//创建模板，参考jsp
			//从消息中获得itemId
			TextMessage textMessage=(TextMessage) message;
			String text=textMessage.getText();
			Long itemId=new Long(text);
			//等待事务提交
			Thread.sleep(1000);
			//由商品ID查询商品信息，商品描述
			TbItem tbItem=itemService.getItemById(itemId);
			Item item=new Item(tbItem);
			TbItemDesc itemDesc=itemService.getItemDescById(itemId);
			//创建数据集，封装商品数据
			Map data=new HashMap<>();
			data.put("item", item);
			data.put("itemDesc", itemDesc);
			//加载模板对象
			Configuration configuration=freeMarkerConfigurer.getConfiguration();
			Template template=configuration.getTemplate("item.ftl");
			//创建输出流，指定输出目录及文件名
			Writer out=new FileWriter(HTML_GEN_PATH + itemId + ".html");
			//生成静态页面
			template.process(data, out);
			//关闭流
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
