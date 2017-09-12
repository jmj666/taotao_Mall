package cn.e3mall.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
//图片上传
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;

@Controller
public class PictureController {
	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	
	@RequestMapping("/pic/upload")
	public String fileUpload(MultipartFile uploadFile)
	{
		try
		{
		//取文件的扩展名
		String originalFilename=uploadFile.getOriginalFilename();
		String extName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		//创建FastDFS客户端
		FastDFSClient fastDFSClient=new FastDFSClient("classpath:conf/client.conf");
		//执行上传处理
		String path=fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
		//拼接返回的URL和ip地址，成为完整的URL
		String url=IMAGE_SERVER_URL+path;
		//返回map
		Map result=new HashMap<>();
		result.put("error", 0);
		result.put("url", url);
		
		return JsonUtils.objectToJson(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Map result=new HashMap<>();
			result.put("error", 1);
			result.put("message", "图片上传failed");
			return JsonUtils.objectToJson(result);
		}
		
	}
	
	
}
