package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Controller;
import org.smart4j.framework.annotation.Service;
import org.smart4j.framework.util.ClassUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 类操作助手类
 * 根据配置，获取所有类，
 * 只能完成加载，但是不能完成所有类的实例化
 * Created by CaiDongYu on 2016/4/8.
 */
public final class ClassHelper {

    /**
     * 存放所有加载的类
     */
    private static final Set<Class<?>> CLASS_SET;

    static{
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    /**
     * 获取所有加载的类
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }


    /**
     * 获取加载的类中，所有的Service
     * TODO:待优化，不用重复获取
     */
    public static Set<Class<?>> getServiceClassSet(){
        Set<Class<?>> classSet = CLASS_SET.stream().filter(cls -> cls.isAnnotationPresent(Service.class)).collect(Collectors.toSet());
        return classSet;
    }

    /**
     * 获取加载的类中，所有的Controller
     * TODO:待优化，不用重复获取
     */
    public static Set<Class<?>> getControllerClassSet(){
        Set<Class<?>> classSet = CLASS_SET.stream().filter(cls -> cls.isAnnotationPresent(Controller.class)).collect(Collectors.toSet());
        return classSet;
    }

    /**
     * 获取所有的 Bean，包括Controller和Service
     * TODO:待优化，不用重复获取
     */

    public static Set<Class<?>> getBeanClassSet(){
        Set<Class<?>> beanClassSet = new HashSet<>();
        beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getControllerClassSet());
        return beanClassSet;
    }
}
