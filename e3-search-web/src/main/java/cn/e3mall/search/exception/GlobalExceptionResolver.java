package cn.e3mall.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

//全局异常处理器
public class GlobalExceptionResolver implements HandlerExceptionResolver{

	Logger logger=LoggerFactory.getLogger(GlobalExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//写日志文件
		logger.error("系统发生异常",ex);
		//展示错误页面
		ModelAndView modelAndView=new ModelAndView();
		modelAndView.addObject("message", "系统发生异常，请稍后重试");
		modelAndView.setViewName("error/exception");
		return modelAndView;
	}

}
