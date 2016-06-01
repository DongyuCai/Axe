package org.axe.home.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.mvc.Interceptor;

public class HomeInterceptor implements Interceptor{
	
	private boolean JW_HOME = false;
	@Override
	public void init() {
		JW_HOME = ConfigHelper.getJwHome();
	}

	@Override
	public boolean doInterceptor(HttpServletRequest request, HttpServletResponse response, Param param,
			Handler handler) {
		return JW_HOME;
	}

}
