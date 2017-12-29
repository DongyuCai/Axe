package org.axe.interface_.mvc;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;


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

	/**
	 * 执行过滤
	 * @param request
	 * @param response
	 * @param param 请求参数
	 * @param handler 请求action Handler
	 */
	public boolean doFilter(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler) throws RestException;
	
	/**
	 * 执行收尾
	 */
	public void doEnd(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler);
}
