package org.test_jw.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.bean.Handler;
import org.jw.bean.Param;
import org.jw.exception.RestException;
import org.jw.interface_.Filter;

public class TestFilter2 implements Filter{

	@Override
	public int setLevel() {
		return 2;
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

}
