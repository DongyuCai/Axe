package org.jw.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.bean.Handler;
import org.jw.bean.Param;
import org.jw.exception.RestException;


public interface Filter {
	/**
	 * 层级 执行顺序从小到大
	 */
	public int setLevel();
	/**
	 * 匹配mappingPath方法的正则
	 */
	public Pattern setMappingPathPattern();

	public boolean doFilter(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler) throws RestException;
	
}
