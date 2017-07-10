package org.axe.home.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.View;
import org.axe.exception.RedirectorInterrupt;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.ioc.BeanHelper;
import org.axe.home.service.HomeService;
import org.axe.interface_.mvc.Interceptor;
import org.axe.util.CollectionUtil;

public class SignInInterceptor implements Interceptor{
	
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

}
