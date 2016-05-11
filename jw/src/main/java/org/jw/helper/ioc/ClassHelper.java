package org.jw.helper.ioc;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jw.annotation.ioc.Component;
import org.jw.annotation.ioc.Controller;
import org.jw.annotation.ioc.Service;
import org.jw.constant.ConfigConstant;
import org.jw.helper.base.ConfigHelper;
import org.jw.util.ClassUtil;

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
    private static final Set<Class<?>> CLASS_SET = new HashSet<>();

    static{
    	String jwPackage = "org.jw";
        //客户自定义应用扫描包路径
        String appBasePackage = ConfigHelper.getAppBasePackage();
        String[] basePackages = appBasePackage.split(",");
        for(String basePackage:basePackages){
        	if(basePackage.startsWith(jwPackage)){
        		throw new RuntimeException(ConfigConstant.APP_BASE_PACKAGE+":"+"不可以使用"+jwPackage+"开头,"+jwPackage+"被框架保留!");
        	}
        	CLASS_SET.addAll(ClassUtil.getClassSet(basePackage));
        }
        //增加jw框架包路径
        CLASS_SET.addAll(ClassUtil.getClassSet(jwPackage));
    }

    /**
     * 获取所有加载的类
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    public static Set<Class<?>> getComponentClassSet(){
    	Set<Class<?>> classSet = CLASS_SET.stream().filter(cls -> cls.isAnnotationPresent(Component.class)).collect(Collectors.toSet());
        return classSet;
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
        beanClassSet.addAll(getComponentClassSet());
        beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getControllerClassSet());
        return beanClassSet;
    }

    /**
     * 获取某父类（或接口）的所有子类（或实现类）
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass){
        Set<Class<?>> classSet = CLASS_SET.stream().filter(cls -> superClass.isAssignableFrom(cls) && !superClass.equals(cls)).collect(Collectors.toSet());
        return classSet;
    }

    /**
     * 获取带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass){
        Set<Class<?>> classSet = CLASS_SET.stream().filter(cls -> cls.isAnnotationPresent(annotationClass)).collect(Collectors.toSet());
        return classSet;
    }
}
