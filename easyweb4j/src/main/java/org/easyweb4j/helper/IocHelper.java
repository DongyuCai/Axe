package org.easyweb4j.helper;

import java.lang.reflect.Field;
import java.util.Map;

import org.easyweb4j.annotation.Autowired;
import org.easyweb4j.util.ArrayUtil;
import org.easyweb4j.util.CollectionUtil;
import org.easyweb4j.util.ReflectionUtil;

/**
 * 依赖注入 助手类
 * Created by CaiDongYu on 2016/4/9.
 */
public final class IocHelper {

    static{
        //获取所有 Bean 与 Bean 实例之间的映射 BEAN_MAP
        Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)){
            //遍历 Bean Map
            for (Map.Entry<Class<?>,Object> beanEntry:beanMap.entrySet()){
                //从 BeanMap 中 获取 Bean 类与 Bean 实例
                Class<?> beanClass =  beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                //获取 Bean  类 定义的所有成员变量 （ Bean Field )
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)){
                    //遍历 bBean Field
                    for (Field beanField:beanFields){
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
