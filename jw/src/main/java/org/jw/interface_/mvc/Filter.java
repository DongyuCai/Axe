package org.jw.interface_.mvc;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.bean.mvc.Handler;
import org.jw.bean.mvc.Param;
import org.jw.exception.RestException;


/**
 * 过滤器 接口
 * 提供Controller之前的请求过滤操作
 * 也可以结合  @FilterFuckOff 注解使用来屏蔽过滤器
 * Created by CaiDongYu on 2016/4/9.
 */
public interface Filter {
	/**
	 * 初始化
	 */
	public void init();
	
	/**
	 * 层级 执行顺序从小到大
	 */
	public int setLevel();
	/**
	 * 匹配mappingPath方法的正则
	 */
	public Pattern setMapping();
	
	/**
	 * 不需要匹配mappingPath方法的正则
	 */
	public Pattern setNotMapping();

	public boolean doFilter(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler) throws RestException;
	
}
