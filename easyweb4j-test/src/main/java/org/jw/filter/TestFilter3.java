package org.jw.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.bean.Handler;
import org.jw.bean.Param;
import org.jw.exception.RestException;
import org.jw.filter.Filter;

public class TestFilter3 implements Filter{

	@Override
	public int setLevel() {
		return 3;
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
