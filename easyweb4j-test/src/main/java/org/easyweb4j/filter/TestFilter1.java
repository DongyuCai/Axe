package org.easyweb4j.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easyweb4j.bean.Handler;
import org.easyweb4j.bean.Param;
import org.easyweb4j.exception.RestException;
import org.easyweb4j.filter.Filter;

public class TestFilter1 implements Filter{

	@Override
	public int setLevel() {
		return 1;
	}

	@Override
	public Pattern setMappingPathPattern() {
		return Pattern.compile("^.*$");
	}

	@Override
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler)
			throws RestException {
		System.out.println(setLevel()+"=="+param);
		return true;
	}

}
