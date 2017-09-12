package cn.e3mall.controller;
//查询商品导入索引库

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.search.service.SearchItemService;

@Controller
public class SearchItemController {
	
	@Autowired
	private SearchItemService searchItemService;
	
	@RequestMapping("index/item/import")
	@ResponseBody
	public E3Result importItemIndex()
	{
		E3Result result=searchItemService.importItems();
		return result;
	}
}
