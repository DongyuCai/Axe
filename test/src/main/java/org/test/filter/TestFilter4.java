package org.test.filter;

import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;

public class TestFilter4 implements Filter{

	@Override
	public int setLevel() {
		return 4;
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
			ServletInputStream in = request.getInputStream();
			byte[] data = new byte[1024];
			int len = 0;
			while((len = in.read(data, 0, data.length)) > 0){
				System.out.println("read:"+len);
			}
			
		} catch (Exception e) {}
		
		return true;
	}

	@Override
	public void init() {}

	@Override
	public void doEnd() {
		// TODO Auto-generated method stub
		
	}

}
