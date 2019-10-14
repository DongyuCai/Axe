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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.ExceptionHolder;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Header;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.ResultHolder;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;

/**
 * Header过滤器
 * 抽象类，需要继承并实现doFilter方法才可使用
 * @author CaiAdongyu
 */
public abstract class HeaderFilter implements Filter {

	@Override
	public void init() {
		
	}

	@Override
	public int setLevel() {
		return 1;
	}
/*
	@Override
	public Pattern setMapping() {
		return Pattern.compile("^.*$");
	}

	@Override
	public Pattern setNotMapping() {
		return null;
	}
*/

	@Override
	public boolean mapping(String requestMethod, String mappingPath) {
		return true;//匹配所有
	}
	
	@Override
	public final boolean doFilter(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler)
			throws RestException {
		Header[] headers = headers();
		
		if(headers != null){
			for(Header header:headers){
				if(header != null){
					String value = request.getHeader(header.getName());
					header.setValue(value);
				}
			}
		}
		
		return doFilter(headers, request, response, param, handler);
	}

	@Override
	public void doEnd(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler,
			ResultHolder resultHolder, ExceptionHolder exceptionHolder) {}

	//Header Filter的实现类必须实现如下
	public abstract Header[] headers();
	
	public abstract boolean doFilter(Header[] headers,HttpServletRequest request, HttpServletResponse response, Param param, Handler handler);
}
