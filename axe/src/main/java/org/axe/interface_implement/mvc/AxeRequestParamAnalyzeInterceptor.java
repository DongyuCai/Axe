package org.axe.interface_implement.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.interface_.mvc.Interceptor;

/**
 * Axe 请求参数解析的Interceptor
 * 已经有了AxeRequestParamAnalyzeFilter了，功能类似，但是Filter是全局的
 * Interceptor可以照顾到那些FuckOff了Filter的类
 */
public class AxeRequestParamAnalyzeInterceptor implements Interceptor {

	@Override
	public void init() {}

	@Override
	public boolean doInterceptor(HttpServletRequest request, HttpServletResponse response, Param param,
			Handler handler) {
		AxeRequestParamAnalyzeFilter.analyzeRequestParam(request, param, handler);
		return true;
	}
}
