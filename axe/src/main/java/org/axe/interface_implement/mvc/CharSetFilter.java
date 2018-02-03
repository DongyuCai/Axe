package org.axe.interface_implement.mvc;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;

public abstract class CharSetFilter implements Filter {

	@Override
	public void init() {
		
	}

	@Override
	public int setLevel() {
		return -1;
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
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}


	@Override
	public void doEnd(HttpServletRequest arg0, HttpServletResponse arg1, Param arg2, Handler arg3) {
	}

}
