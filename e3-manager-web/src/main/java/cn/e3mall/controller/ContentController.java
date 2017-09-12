package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
//内容列表查询
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

@Controller
@RequestMapping("/content")
public class ContentController {
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/query/list")
	@ResponseBody
	public EasyUIDataGridResult getConList(Long id,Integer page,Integer rows)
	{
		EasyUIDataGridResult result=contentService.getContentList(id, page, rows);
		return result;
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public E3Result addCon(TbContent tbContent)
	{
		E3Result res=contentService.addContent(tbContent);
		return res;
	}
}
