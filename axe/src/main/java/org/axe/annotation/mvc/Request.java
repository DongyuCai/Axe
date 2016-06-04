package org.axe.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axe.constant.CharacterEncoding;
import org.axe.constant.ContentType;
import org.axe.constant.RequestMethod;

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
     * 返回结果  MIME 类型
     */
    ContentType contentType() default ContentType.APPLICATION_JSON;
    
    /**
     * 编码类型
     */
    CharacterEncoding characterEncoding() default CharacterEncoding.UTF_8;
}
