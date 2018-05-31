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
package org.axe.interface_implement.mvc;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.ExceptionHolder;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.ResultHolder;
import org.axe.exception.RestException;
import org.axe.helper.mvc.FormRequestHelper;
import org.axe.interface_.mvc.Filter;
import org.axe.util.AjaxRequestUtil;

/**
 * Axe 请求参数解析Filter
 * 这是Axe 提供的Filter，层级为0
 * 所以如果想在解析参数之前来执行一些操作，可以把自己定义的Filter层级设置为小于0
 * 层级大于0的自定义Filter，都在此之后执行
 */
public class AxeRequestParamAnalyzeFilter implements Filter {

	@Override
	public void init() {
		
	}

	@Override
	public int setLevel() {
		return 0;
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
		//解析resquest中参数
		analyzeRequestParam(request, param, handler);
		return true;
	}
	
	private void analyzeRequestParam(HttpServletRequest request, Param param, Handler handler){
		if(FormRequestHelper.isMultipart(request)){
            //如果是文件上传
            FormRequestHelper.initParam(param,request,param.getRequestPath(),handler.getMappingPath());
        }else{
            //如果不是
            try {
				AjaxRequestUtil.initParam(param,request,param.getRequestPath(),handler.getMappingPath());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RestException(RestException.SC_INTERNAL_SERVER_ERROR,e.getMessage());
			}
        }
	}
	

	@Override
	public void doEnd(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler,
			ResultHolder resultHolder, ExceptionHolder exceptionHolder) {}

	
}
