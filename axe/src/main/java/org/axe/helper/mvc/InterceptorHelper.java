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
package org.axe.helper.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.Interceptor;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;

/**
 * 拦截器 助手类
 * @author CaiDongyu on 2016年5月30日 下午12:21:51.
 */
public final class InterceptorHelper implements Helper{

	private static Map<Class<? extends Interceptor>,Interceptor> INTERCEPTOR_MAP;//不保证顺序
	
	@Override
	public void init() throws Exception{
		synchronized (this) {
			INTERCEPTOR_MAP = new HashMap<>();
			Set<Class<?>> interceptorClassSet = ClassHelper.getClassSetBySuper(Interceptor.class);
	        if(CollectionUtil.isNotEmpty(interceptorClassSet)){
	        	for(Class<?> interceptorClass:interceptorClassSet){
	        		Interceptor interceptor = ReflectionUtil.newInstance(interceptorClass);
	        		interceptor.init();// 初始化Interceptor
	        		INTERCEPTOR_MAP.put(interceptor.getClass(), interceptor);
	        	}
	        }
		}
	}
	
	public static Map<Class<? extends Interceptor>, Interceptor> getInterceptorMap() {
		return INTERCEPTOR_MAP;
	}

	@Override
	public void onStartUp() throws Exception {}
	
}
