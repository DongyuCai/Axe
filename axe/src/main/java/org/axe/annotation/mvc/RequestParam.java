package org.axe.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ActionMethod 获取请求参数的注解
 * TODO:增加default值，暂时不增加，前台传参数就是要规范，不规范怎么行
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
	/**
	 * 参数字段名
	 */
	String value();
	
	/**
	 * 描述
	 */
	String desc() default "";
	
	/**
	 * 是否必填
	 */
	boolean required() default false;
}
