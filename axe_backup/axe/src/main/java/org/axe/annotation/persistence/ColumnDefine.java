package org.axe.annotation.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * #Dao entity 类属性注解
 * 用来给Entity字段做定义
 * 不包含列名定义
 * axe中认为，列名就应该根据字段名按规范映射生成，不应该另作他名
 * 用法比如：
 * 	没有使用@ColumnDefine的字段是这样的：
 * 		private String name;
 *  对应的数据库列默认定义是这样的：
 *  	`name` varchar(255) DEFAULT NULL;
 *  如果加上@ColumnDefine自定义字段类型：
 *  	@ColumnDefine("varchar(10) NOT NULL");
 * 		private String name;
 *  则对于的数据库列定义会是这样：
 *  	`name` varchar(10) NOT NULL;
 *  当然可以写更多的指令，比如long类型可以加上AUTO_INCREMENT，唯一属性可以UNIQUE等等
 *  当与@Id同时存在时，@ColumnDefine优先级更高
 * Created by CaiDongYu on 2016/9/22.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnDefine {
	String value();
}
