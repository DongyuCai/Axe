package org.jw.annotation.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来指定表
 * #Dao entity 类注解
 * 其实就算不指定这个注解，也一样可以用@Sql查询后映射成Bean，
 * 这个注解是为了让@Sql支持类似HQL风格的，基于类的Sql语法，
 * 如果不使用这个注解，@Sql就只能写传统的mysql本地方言，
 * #特殊要求是，加了这个注解的类，全局不能出现相同的两个，即便包路径不同也不行。
 * 因为在@Sql解析，当做HQL的时候，sql里只含有类名，所以全局找Bean的时候，同名会出问题
 * Created by CaiDongYu on 2016/4/8.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	String value();
}
