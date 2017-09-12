package cn.e3mall.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;

/**
 * @author public
 * <p>Title: ItemController </p>
 * <p>Description: 商品详情页展示</p>
 * <p>Company: e3-item-web </p>
 * @version 
 */
@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId,Model model)
	{
		//根据id查询商品信息与商品描述
		TbItem tbItem=itemService.getItemById(itemId);
		Item item=new Item(tbItem);
		TbItemDesc itemDesc=itemService.getItemDescById(itemId);
		//数据传递给页面
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		return "item";
	}
}
