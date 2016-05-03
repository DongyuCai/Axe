package org.jw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    org.jw.constant.RequestMethod method();
}
