package org.easyweb4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.easyweb4j.filter.Filter;

/**
 * 排除过滤器、拦截器注解
 * 应用在那些不需要某些过滤器的Controller方法上
 * 也可以直接暴力加在Controller上，这样方法上的此注解会失效，
 * Controller中所有方法都依照Controller上的此注解配置
 * Created by CaiDongYu on 2016/4/8.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterFuckOff {
	
	/**
	 * 需要排除的过滤器列表
	 * 默认为空，注意注意，为空不是说不排除，而是排除所有！
	 */
	Class<? extends Filter>[] value() default {};
}
