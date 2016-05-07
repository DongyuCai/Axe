package org.jw.interface_;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.bean.Handler;
import org.jw.bean.Param;
import org.jw.exception.RestException;


/**
 * 过滤器 接口
 * 提供Controller之前的请求过滤操作
 * 也可以结合  @FilterFuckOff 注解使用来屏蔽过滤器
 * Created by CaiDongYu on 2016/4/9.
 * TODO:增加 after 方法，支持拦截Controller处理后
 */
public interface Filter {
	/**
	 * 层级 执行顺序从小到大
	 */
	public int setLevel();
	/**
	 * 匹配mappingPath方法的正则
	 * TODO:增加NotMappingPathPattern方法，可以从匹配的结果中抛去不匹配的，比如匹配除登录接口以外的
	 */
	public Pattern setMappingPathPattern();

	public boolean doFilter(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler) throws RestException;
	
}
