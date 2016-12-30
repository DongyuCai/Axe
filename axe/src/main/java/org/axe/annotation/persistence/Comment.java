package org.axe.annotation.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * #Dao entity 类属性注解
 * 用来给Entity字段做描述
 *  当与@ColumnDefine同时存在时，@ColumnDefine优先级更高
 * Created by CaiDongYu on 2016/9/22.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
	String value();
}
