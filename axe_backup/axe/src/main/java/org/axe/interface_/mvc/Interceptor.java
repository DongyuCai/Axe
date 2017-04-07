package org.axe.interface_.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;

/**
 * 拦截器
 * 针对的是方法
 * 多个拦截器之间不区分先后
 */
public interface Interceptor {

	public void init();
	
	public boolean doInterceptor(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler);
}
