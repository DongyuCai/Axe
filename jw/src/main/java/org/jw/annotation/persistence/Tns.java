package org.jw.annotation.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事务注解
 * Created by CaiDongYu on 2016/4/18.
 */
//TODO:目前只支持 method ，要与service注解配合使用，将来改成支持 类全切的，支持多数据源的
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tns {

}
