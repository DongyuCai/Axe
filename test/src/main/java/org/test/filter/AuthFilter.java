package org.test.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;

public class AuthFilter implements Filter{

	@Override
	public void init() {}

	@Override
	public int setLevel() {
		return 0;
	}

	@Override
	public Pattern setMapping() {
		return Pattern.compile("^.*$");
	}

	@Override
	public Pattern setNotMapping() {
		return Pattern.compile("test-login/login");
	}

	@Override
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler)
			throws RestException {
		String axe_token = request.getHeader("axe-token");
		if(axe_token == null || "".equals(axe_token.trim())){
			throw new RestException(RestException.SC_UNAUTHORIZED,"需要登录");
		}else{
			return true;
		}
	}

	@Override
	public void doEnd() {
	}

}
