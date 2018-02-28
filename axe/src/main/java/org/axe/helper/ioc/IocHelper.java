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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.axe.annotation.ioc.Autowired;
import org.axe.interface_.base.Helper;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;

/**
 * 依赖注入 助手类
 * @author CaiDongyu on 2016/4/9.
 */
public final class IocHelper implements Helper{
	
	@Override
	public void init() {
		synchronized (this) {
			//获取所有 Bean 与 Bean 实例之间的映射 BEAN_MAP
	        Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();
	        if (CollectionUtil.isNotEmpty(beanMap)){
	            //遍历 Bean Map
	            for (Map.Entry<Class<?>,Object> beanEntry:beanMap.entrySet()){
	                //从 BeanMap 中 获取 Bean 类与 Bean 实例
	                Class<?> beanClass =  beanEntry.getKey();
	                Object beanInstance = beanEntry.getValue();
	                //获取 Bean  类 定义的所有成员变量 （ Bean Field )
	                List<Field> beanFieldList = ReflectionUtil.getDeclaredFieldsAll(beanClass);
	                if (CollectionUtil.isNotEmpty(beanFieldList)){
	                    //遍历 bBean Field
	                    for (Field beanField:beanFieldList){
	                        if(beanField.isAnnotationPresent(Autowired.class)){
	                            //再 Bean Map 中获取 Bean Field 对应的实例
	                            Class<?> beanFieldClass = beanField.getType();
	                            Object beanFieldInstance = beanMap.get(beanFieldClass);
	                            //通过反射初始化 Bean Field 值
	                            ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
	                        }
	                    }
	                }
	            }
	        }
		}
	}

	@Override
	public void onStartUp() throws Exception {}
}
