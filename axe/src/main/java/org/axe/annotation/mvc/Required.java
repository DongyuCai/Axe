package org.axe.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 必填
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
	
	/**
	 * 必填项列表，默认是空，表示全字段必填，如果指定，则检测指定字段
	 */
	String[] value() default {};
}
