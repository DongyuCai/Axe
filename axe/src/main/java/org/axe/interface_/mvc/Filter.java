/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.interface_.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.ExceptionHolder;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.ResultHolder;
import org.axe.exception.RestException;


/**
 * 过滤器 接口
 * 提供Controller之前的请求过滤操作
 * 也可以结合  @FilterFuckOff 注解使用来屏蔽过滤器
 * @author CaiDongyu on 2016/4/9.
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
//	public Pattern setMapping();
	
	/**
	 * 不需要匹配mappingPath方法的正则
	 */
//	public Pattern setNotMapping();
	
	/**
	 * 是否匹配此Request
	 */
	/**
	 * 匹配mappingPath方法的正则
	 */
	public boolean mapping(String requestMethod, String mappingPath);

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
	public void doEnd(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler,ResultHolder resultHolder,ExceptionHolder exceptionHolder);
}
