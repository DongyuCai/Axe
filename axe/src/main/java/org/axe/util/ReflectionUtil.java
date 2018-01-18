package org.axe.util;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.exception.RedirectorInterrupt;
import org.axe.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类实例化工具
 * 配合类加载工具ClassUtil
 * Created by CaiDongYu on 2016/4/8.
 */
public final class ReflectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    
    public static List<Method> getMethodByAnnotation(Class<?> cls,Class<? extends Annotation> annotationClass){
    	List<Method> result = new ArrayList<Method>();
    	Method[] methods = cls.getDeclaredMethods();
    	for(Method method:methods){
    		if(method.isAnnotationPresent(annotationClass)){
    			result.add(method);
    		}
    	}
    	Class<?> superClass = cls.getSuperclass();
    	if(superClass != null){
    		result.addAll(getMethodByAnnotation(superClass, annotationClass));
    	}
    	return result;
    }
    
    /**
     * 获取Class中的成员变量，如果有父类，一并获取
     * 重写的变量只取子类的
     */
    public static List<Field> getDeclaredFieldsAll(Class<?> cls){
    	return getDeclaredFieldsAll(cls,null);
    }
    		
    public static List<Field> getDeclaredFieldsAll(Class<?> cls, Set<String> withoutFieldNameSet){
    	withoutFieldNameSet = withoutFieldNameSet == null?new HashSet<String>():withoutFieldNameSet;
    	Field[] fields = cls.getDeclaredFields();
    	List<Field> fieldList = new ArrayList<>();
    	//#组成一个字段名的比较串，后面用来比较get方法是不是有意义的get方法
    	for(Field field:fields){
    		if(withoutFieldNameSet.contains(field.getName())) continue;
    		
    		fieldList.add(field);
    		withoutFieldNameSet.add(field.getName());
    	}
    	Class<?> superClass = cls.getSuperclass();
    	if(superClass != null && !superClass.equals(Object.class)){
    		//#迭代父类
    		fieldList.addAll(getDeclaredFieldsAll(superClass,withoutFieldNameSet));
    	}
    	
    	return fieldList;
    }
    
    public static List<EntityFieldMethod> getGetMethodList(Class<?> cls){
    	return getGetMethodList(cls, null);
    }
    /**
     * 自子类向父类，寻找所有字段以及对应的Get方法
     * 子类中与父类同名的字段，取子类，舍父类
     */
    public static List<EntityFieldMethod> getGetMethodList(Class<?> cls, Set<String> withoutFieldNameSet){
    	withoutFieldNameSet = withoutFieldNameSet == null?new HashSet<String>():withoutFieldNameSet;
    	Field[] fields = cls.getDeclaredFields();
    	Map<String,Field> fieldMap = new HashMap<>();
    	//#组成一个字段名的比较串，后面用来比较get方法是不是有意义的get方法
    	for(Field field:fields){
    		fieldMap.put(field.getName(), field);
    	}
    	Method[] methodAry =  cls.getDeclaredMethods();
    	List<EntityFieldMethod> getMethodList = new ArrayList<>();
    	//#获取Entity类的get方法
    	for(Method method:methodAry){
    		//#get开头
    		if(!method.getName().startsWith("get")) continue;
    		String fieldName = method.getName().substring(3);
    		String fieldName1 = fieldName.substring(0,1);
    		String fieldName2 = fieldName.substring(1);
    		//#get之后的字符串，必须和class类成员变量对应，第一个字符小写大写对应
    		fieldName = fieldName1.toLowerCase()+fieldName2;
    		Field field = fieldMap.get(fieldName);
    		if(field == null) continue;
    		//#无参
    		if(method.getParameterTypes().length > 0) continue;
    		//#排除的字段
    		if(withoutFieldNameSet.contains(fieldName)) continue;
    		
    		getMethodList.add(new EntityFieldMethod(field, method));
    		withoutFieldNameSet.add(fieldName);
    	}
    	
    	Class<?> superClass = cls.getSuperclass();
    	if(superClass != null && !superClass.equals(Object.class)){
    		//#迭代父类
    		getMethodList.addAll(getGetMethodList(superClass,withoutFieldNameSet));
    	}
    	
    	return getMethodList;
    }

    public static List<EntityFieldMethod> getSetMethodList(Class<?> cls){
    	return getSetMethodList(cls, null);
    }

    /**
     * 自子类向父类，寻找所有字段以及对应的Set方法
     * 子类中与父类同名的字段，取子类，舍父类
     */
    public static List<EntityFieldMethod> getSetMethodList(Class<?> cls, Set<String> withoutFieldNameSet){
    	withoutFieldNameSet = withoutFieldNameSet == null?new HashSet<String>():withoutFieldNameSet;
    	Field[] fields = cls.getDeclaredFields();
    	Map<String,Field> fieldMap = new HashMap<>();
    	//#组成一个字段名的比较串，后面用来比较get方法是不是有意义的get方法
    	for(Field field:fields){
    		fieldMap.put(field.getName(), field);
    	}
    	Method[] methodAry = cls.getDeclaredMethods();
		List<EntityFieldMethod> etityFieldMethodList = new ArrayList<>();
		for(Method method:methodAry){
			//#set开头
			if(!method.getName().startsWith("set")) continue;
    		String fieldName = method.getName().substring(3);
    		String fieldName1 = fieldName.substring(0,1);
    		String fieldName2 = fieldName.substring(1);
    		//#set之后的字符串，必须和class类成员变量对应，第一个字符小写大写对应
    		fieldName = fieldName1.toLowerCase()+fieldName2;
    		Field field = fieldMap.get(fieldName);
    		if(field == null) continue;
			//#带1个参数，类型与Field一至
    		Class<?>[] parameterAry = method.getParameterTypes();
    		if(parameterAry == null || parameterAry.length != 1) continue;
    		if(!compareType(parameterAry[0], field.getType())) continue;
    		//#排除的字段
    		if(withoutFieldNameSet.contains(fieldName)) continue;
    		
    		etityFieldMethodList.add(new EntityFieldMethod(field, method));
    		withoutFieldNameSet.add(fieldName);
		}

    	Class<?> superClass = cls.getSuperclass();
    	if(superClass != null && !superClass.equals(Object.class)){
    		//#迭代父类
    		etityFieldMethodList.addAll(getSetMethodList(superClass,withoutFieldNameSet));
    	}
		
		return etityFieldMethodList;
    }
    
    /**
     * 比较两个类，是否是同一个类
     */
    public static boolean compareType(Class<?> cls1,Class<?> cls2){
    	return cls1 == null?false:cls1.equals(cls2);
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
        	MethodHandle mh=MethodHandles.lookup().unreflect(method);
        	ArrayList<Object> argList = new ArrayList<>();
        	argList.add(obj);
        	if(args != null){
        		for(Object arg:args){
        			argList.add(arg);
        		}
        	}
        	result = mh.invokeWithArguments(argList);
		} catch (Throwable cause) {
			LOGGER.error("invoke method failure,method : "+method+", args : "+args,cause);
			if(cause instanceof RestException){
				//Rest中断异常，需要返回前台异常信息
				throw (RestException)cause;
			}else if(cause instanceof RedirectorInterrupt){
				//重定向中断，需要跳转
				throw (RedirectorInterrupt)cause;
			}else{
				throw new RuntimeException(cause);
			}
		}
        
        /*try {
			result = method.invoke(obj,args);
		} catch (IllegalAccessException e) {
			LOGGER.error("invoke method failure,method : "+method+", args : "+args,e);
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("invoke method failure,method : "+method+", args : "+args,e);
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			LOGGER.error("invoke method failure,method : "+method+", args : "+args,e);
			Throwable cause = e.getCause();
			if(cause != null){
				if(cause instanceof RestException){
					//Rest中断异常，需要返回前台异常信息
					throw (RestException)e.getCause();
				}else if(cause instanceof RedirectorInterrupt){
					//重定向中断，需要跳转
					throw (RedirectorInterrupt)e.getCause();
				}else{
					throw new RuntimeException(cause);
				}
			}else{
				throw new RuntimeException(e);
				
			}
		}*/
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
