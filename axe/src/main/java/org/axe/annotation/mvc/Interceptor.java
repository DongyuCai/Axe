package org.axe.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拦截器 注解
 * 拦截器没有FuckOff排除
 * Created by CaiDongYu on 2016年5月30日 下午12:16:43.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
	/**
	 * 需要排除的过滤器列表
	 * 默认为空，注意注意，为空不是说不排除，而是排除所有！
	 */
	Class<? extends org.axe.interface_.mvc.Interceptor>[] value();
}
