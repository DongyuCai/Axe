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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.Listener;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;

/**
 * Listener 启动助手类
 * @author CaiDongyu on 2016年6月7日 下午1:35:37.
 */
public final class ListenerHelper implements Helper{
	
	private static List<Listener> LISTENER_LIST;
	
	@Override
	public void init() throws Exception {
		synchronized (this) {
			LISTENER_LIST = new LinkedList<>();
			Set<Class<?>> classSet = ClassHelper.getClassSetBySuper(Listener.class);
			if(CollectionUtil.isNotEmpty(classSet)){
				for(Class<?> listenerClass:classSet){
					Listener listener = ReflectionUtil.newInstance(listenerClass);
					BeanHelper.setBean(listenerClass, listener);
					if(LISTENER_LIST.size() == 0){
						LISTENER_LIST.add(listener);
					}else{
						int i=0;
						for(;i<LISTENER_LIST.size();i++){
							if(listener.index()<LISTENER_LIST.get(i).index()){
								break;
							}
						}
						if(i<LISTENER_LIST.size()){
							LISTENER_LIST.add(i,listener);
						}else{
							LISTENER_LIST.add(listener);
						}
					}
				}
			}
		}
	}

	@Override
	public void onStartUp() throws Exception {
		for(Listener listener:LISTENER_LIST){
			listener.init();
		}
	}

	public static List<Listener> getListenerList() {
		return LISTENER_LIST;
	}


}
