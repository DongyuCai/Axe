package org.axe.annotation.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不持久化注解
 * Created by CaiDongYu on 2017/6/7.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {

	/**
	 * 是否存
	 */
	boolean save() default false;
	
	/**
	 * 是否查
	 */
	boolean query() default false;
}
