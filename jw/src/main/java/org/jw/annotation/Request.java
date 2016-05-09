package org.jw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jw.constant.RequestMethod;

/**
 * 控制器方法注解
 * Created by CaiDongYu on 2016/4/8.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {

    /**
     * 请求路径
     */
    String value();
    
    /**
     * 请求类型
     */
    RequestMethod method();
    
    /**
     * 返回结果类型
     */
    String contentType() default "application/json";
    
    /**
     * 编码类型
     */
    String characterEncoding() default "UTF-8";
}
