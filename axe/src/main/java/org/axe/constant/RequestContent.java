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
package org.axe.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求处理的上下文对象
 * 生命周期在一次请求
 * 这个上下文，是线程安全的
 * 即便多次请求出现线程被复用，也不会有问题
 * @author CaiDongyu 2020/3/21
 */
public final class RequestContent {
	private static ThreadLocal<Map<String, Object>> CONTENT = new ThreadLocal<>();

	public synchronized static void setParam(String name,Object value){
		Map<String, Object> map = CONTENT.get();
		if(map == null){
			map = new HashMap<>();
		}
		map.put(name, value);
		CONTENT.set(map);
	}

    @SuppressWarnings("unchecked")
	public synchronized static <T> T getParam(String name){
		Map<String, Object> map = CONTENT.get();
		if(map == null){
			return null;
		}
		return (T)map.get(name);
	}
	
    public synchronized static void clean(){
    	CONTENT.remove();
    }
}
