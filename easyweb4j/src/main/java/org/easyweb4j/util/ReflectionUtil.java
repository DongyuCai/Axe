package org.easyweb4j.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类实例化工具
 * 配合类加载工具ClassUtil
 * Created by CaiDongYu on 2016/4/8.
 */
public final class ReflectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);
    
    /**
     * 比较两个类，是否是同一个类
     */
    public static boolean compareType(Class<?> cls1,Class<?> cls2){
    	return cls1.getName().equals(cls2.getName());
    }
    
    /**
     * 创建实例
     */
    @SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> cls){
        Object instance;
        try {
            instance = cls.newInstance();
        } catch (Exception e) {
            LOGGER.error("new instance failure",e);
            throw new RuntimeException(e);
        }
        return (T)instance;
    }

    /**
     * 调用方法
     */
    public static  Object invokeMethod(Object obj,Method method,Object ...args){
        Object result;
        method.setAccessible(true);
        try {
            result = method.invoke(obj,args);
        } catch (Exception e) {
            LOGGER.error("invoke method failure",e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 设置成员变量的值
     */
    public static void setField(Object obj, Field field, Object value){
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (Exception e) {
            LOGGER.error("set field failure",e);
            throw new RuntimeException(e);
        }
    }
}
