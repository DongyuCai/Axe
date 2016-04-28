package org.easyweb4j.filter;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easyweb4j.bean.Handler;
import org.easyweb4j.bean.Param;
import org.easyweb4j.exception.RestException;


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
