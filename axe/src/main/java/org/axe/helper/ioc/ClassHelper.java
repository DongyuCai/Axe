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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.axe.annotation.ioc.Component;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.ioc.Service;
import org.axe.constant.ConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.Listener;
import org.axe.interface_.mvc.Timer;
import org.axe.util.ClassUtil;
import org.axe.util.StringUtil;

/**
 * 类操作助手类
 * 根据配置，获取所有类，
 * 只能完成加载，但是不能完成所有类的实例化
 * @author CaiDongyu on 2016/4/8.
 */
public final class ClassHelper implements Helper{

    /**
     * 存放所有加载的类
     * TODO:CLASS_SET占用的空间其实在框架初始化之后，就没用了。
     */
    private static Set<Class<?>> CLASS_SET;
    
    @Override
    public void init() throws Exception{
    	synchronized (this) {
    		CLASS_SET = new HashSet<>();
        	
        	String axePackage = "org.axe";
            //客户自定义应用扫描包路径
            String appBasePackage = ConfigHelper.getAppBasePackage();
            if(StringUtil.isNotEmpty(appBasePackage)){
            	String[] basePackages = appBasePackage.split(",");
            	for(String basePackage:basePackages){
            		if(basePackage.startsWith(axePackage)){
            			throw new Exception(ConfigConstant.APP_BASE_PACKAGE+":"+"不可以使用"+axePackage+"开头,"+axePackage+"被框架保留!");
            		}
            		CLASS_SET.addAll(ClassUtil.getClassSet(basePackage));
            	}
            }
            //增加axe框架包路径
            CLASS_SET.addAll(ClassUtil.getClassSet(axePackage));
    	}
    }

    /**
     * 获取所有加载的类
     * 这是个极其消耗资源的操作，个人认为
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    public static Set<Class<?>> getComponentClassSet(){
    	Set<Class<?>> classSet = new HashSet<>();
    	for(Class<?> cls:CLASS_SET){
    		if(cls.isAnnotationPresent(Component.class)){
    			classSet.add(cls);
    		}
    	}
        return classSet;
    }

    /**
     * 获取加载的类中，所有的Service
     */
    public static Set<Class<?>> getServiceClassSet(){
    	Set<Class<?>> classSet = new HashSet<>();
    	for(Class<?> cls:CLASS_SET){
    		if(cls.isAnnotationPresent(Service.class)){
    			classSet.add(cls);
    		}
    	}
        return classSet;
    }

    /**
     * 获取加载的类中，所有的Controller
     */
    public static Set<Class<?>> getControllerClassSet(){
    	Set<Class<?>> classSet = new HashSet<>();
    	for(Class<?> cls:CLASS_SET){
    		if(cls.isAnnotationPresent(Controller.class)){
    			classSet.add(cls);
    		}
    	}
        return classSet;
    }
    
    /**
     * 获取所有的 Bean
     * TODO(OK):待优化，不用重复获取
     */
    public static Set<Class<?>> getBeanClassSet(){
        Set<Class<?>> beanClassSet = new HashSet<>();
        for(Class<?> cls:CLASS_SET){
    		//#Component.class
    		if(cls.isAnnotationPresent(Component.class)){
    			beanClassSet.add(cls);
    		}
        	//#Controller
    		if(cls.isAnnotationPresent(Controller.class)){
    			beanClassSet.add(cls);
    		}
    		//#Service
    		if(cls.isAnnotationPresent(Service.class)){
    			beanClassSet.add(cls);
    		}
    		//#Timer
    		if(Timer.class.isAssignableFrom(cls)){
    			beanClassSet.add(cls);
    		}
    		//#Listener
    		if(Listener.class.isAssignableFrom(cls)){
    			beanClassSet.add(cls);
    		}
    	}
//        beanClassSet.addAll(getComponentClassSet());
//        beanClassSet.addAll(getServiceClassSet());
//        beanClassSet.addAll(getControllerClassSet());
        return beanClassSet;
    }

    /**
     * 获取某父类（或接口）的所有子类（或实现类）
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass){
    	Set<Class<?>> classSet = new HashSet<>();
    	for(Class<?> cls:CLASS_SET){
    		if(superClass.isAssignableFrom(cls) && !superClass.equals(cls)){
    			classSet.add(cls);
    		}
    	}
        return classSet;
    }

    /**
     * 获取带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass){
    	Set<Class<?>> classSet = new HashSet<>();
    	for(Class<?> cls:CLASS_SET){
    		if(cls.isAnnotationPresent(annotationClass)){
    			classSet.add(cls);
    		}
    	}
        return classSet;
    }
    
	@Override
	public void onStartUp() throws Exception {}
}
