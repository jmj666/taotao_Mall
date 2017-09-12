package cn.e3mall.controller;
//商品管理,根据ID获取对象，给商品添加功能
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;

@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId)
	{
		TbItem tbItem=itemService.getItemById(itemId);
		return tbItem;
	}
	
	@RequestMapping("item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows)
	{
		EasyUIDataGridResult result=itemService.getItemList(page, rows);
		return result;
	}
	
	@RequestMapping("item/save")
	@ResponseBody
	public E3Result saveItem(TbItem tbItem,String desc)
	{
		E3Result result=itemService.addItem(tbItem,desc);
		return result;
	}
}
