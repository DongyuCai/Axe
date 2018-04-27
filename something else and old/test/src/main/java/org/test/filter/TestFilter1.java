package org.test.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;

public class TestFilter1 implements Filter{

	@Override
	public int setLevel() {
		return 1;
	}

	@Override
	public Pattern setMapping() {
		return Pattern.compile("^.*$");
	}
	
	@Override
	public Pattern setNotMapping() {
		return null;
	}

	@Override
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler)
			throws RestException {
		System.out.println(setLevel()+"=="+param);
		return true;
	}

	@Override
	public void init() {}

	@Override
	public void doEnd() {
		// TODO Auto-generated method stub
		
	}

}
