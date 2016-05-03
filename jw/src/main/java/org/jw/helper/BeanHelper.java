package org.jw.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jw.util.ReflectionUtil;

/**
 * Bean 助手
 * ClassHelper 负责加载
 * ReflectionUtil 负责实例化
 * BeanHelper 负责托管所有Bean （Controller、Service）
 * BeanHelper 只是实例化，但是没有注入依赖，依赖注入靠IocHelper
 * Created by CaiDongYu on 2016/4/9.
 */
public final class BeanHelper {
    /**
     * 定义 Bean 映射（用于存放 Bean 类与 Bean 实例的映射
     */
    private static final Map<Class<?>,Object> BEAN_MAP = new HashMap<>();
    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for(Class<?> beanClass:beanClassSet){
            Object obj = ReflectionUtil.newInstance(beanClass);
            BEAN_MAP.put(beanClass, obj);
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
}
