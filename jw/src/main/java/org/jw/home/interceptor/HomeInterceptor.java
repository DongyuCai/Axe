package org.jw.home.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.bean.mvc.Handler;
import org.jw.bean.mvc.Param;
import org.jw.helper.base.ConfigHelper;
import org.jw.interface_.mvc.Interceptor;

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
