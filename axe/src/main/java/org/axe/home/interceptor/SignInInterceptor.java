/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.home.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.ExceptionHolder;
import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.ResultHolder;
import org.axe.bean.mvc.View;
import org.axe.exception.RedirectorInterrupt;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.ioc.BeanHelper;
import org.axe.home.service.HomeService;
import org.axe.interface_.mvc.Interceptor;
import org.axe.util.CollectionUtil;

public final class SignInInterceptor implements Interceptor{
	
	private boolean AXE_SIGN_IN = false;
	
	@Override
	public void init() {
		AXE_SIGN_IN = ConfigHelper.getAxeSignIn();
	}

	@Override
	public boolean doInterceptor(HttpServletRequest request, HttpServletResponse response, Param param,
			Handler handler) {
		//#如果不需要登录操作，直接放行
		if(!AXE_SIGN_IN){
			return true;
		}
		
		//##需要登录
		if("/axe/sign-in".equals(handler.getMappingPath())){
			//#登录接口肯定不需要登录 /axe/sign-in
			return true;
		}
		
		//##查看是否有token参数
		List<FormParam> tokenList = param.getFieldMap().get("token");
		if(CollectionUtil.isNotEmpty(tokenList)){
			String token = tokenList.get(0).getFieldValue();
			boolean success = BeanHelper.getBean(HomeService.class).checkPrivateToken(request, token);
			if(success){
				//###通过验证
				return true;
			}
		}
		
		throw new RedirectorInterrupt(new View("/axe/sign-in"));
	}

	@Override
	public void doEnd(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler,
			ResultHolder resultHolder, ExceptionHolder exceptionHolder) {}

}
