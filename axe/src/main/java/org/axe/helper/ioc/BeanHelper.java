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
package org.axe.helper.ioc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.AfterBeanInited;
import org.axe.interface_.mvc.AfterClassLoaded;
import org.axe.interface_.mvc.AfterConfigLoaded;
import org.axe.util.ReflectionUtil;

/**
 * Bean 助手
 * ClassHelper 负责加载
 * ReflectionUtil 负责实例化
 * BeanHelper 负责托管所有Bean （Controller、Service）
 * BeanHelper 只是实例化，但是没有注入依赖，依赖注入靠IocHelper
 * @author CaiDongyu on 2016/4/9.
 */
public final class BeanHelper implements Helper{
    /**
     * 定义 Bean 映射（用于存放 Bean 类与 Bean 实例的映射
     */
    private static Map<Class<?>,Object> BEAN_MAP;

    private static List<AfterBeanInited> AFTER_BEAN_INITED_LIST = new ArrayList<>();
    
    public static void addAfterBeanInitedCallback(AfterBeanInited callback){
    	synchronized (AFTER_BEAN_INITED_LIST) {
    		AFTER_BEAN_INITED_LIST.add(callback);
		}
    }
    
    @Override
    public void init() throws Exception{
    	synchronized (this) {
    		BEAN_MAP = new HashMap<>();
        	Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
            for(Class<?> beanClass:beanClassSet){
                Object obj = ReflectionUtil.newInstance(beanClass);
                BEAN_MAP.put(beanClass, obj);
            }
		}

    	//加载完配置后，执行
    	for(AfterBeanInited acl:AFTER_BEAN_INITED_LIST){
    		acl.doSomething(BEAN_MAP);
    	}
    }

    /**
     * 获取 BEAN_MAP
     * 只是实例化，但是没有注入依赖
     */
    public static Map<Class<?>,Object> getBeanMap(){
        return BEAN_MAP;
    }

    /**
     * 获取 Bean 实例
     */
    @SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> cls){
        if(!BEAN_MAP.containsKey(cls)){
            throw new RuntimeException("can not get bean by class:"+cls);
        }
        return (T) BEAN_MAP.get(cls);
    }

    /**
     * 设置 Bean 实例
     */
    public static void setBean(Class<?> cls,Object obj){
        BEAN_MAP.put(cls, obj);
    }

	@Override
	public void onStartUp() throws Exception {}
}
