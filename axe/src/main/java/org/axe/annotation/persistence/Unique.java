package org.axe.annotation.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 唯一键指定 unique key
 * 多个唯一键字段，都加注这个注解即可
 * 注意！：此注解不会对sql层执行有任何影响，具体唯一约束任然要靠mysql自身判断
 * 这个注解目前只是提供给CreateTableUtil生成建表sql时候使用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {

}
