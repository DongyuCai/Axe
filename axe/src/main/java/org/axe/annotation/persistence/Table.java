/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.annotation.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来指定表
 * #Dao entity 类注解
 * 其实就算不指定这个注解，也一样可以用@Sql查询后映射成Bean，
 * 这个注解是为了让@Sql支持类似HQL风格的，基于类的Sql语法，
 * 如果不使用这个注解，@Sql就只能写传统的本地方言，
 * #特殊要求是，加了这个注解的类，全局不能出现相同的两个，即便包路径不同也不行。
 * 因为在@Sql解析，当做HQL的时候，sql里只含有类名，所以全局找Bean的时候，同名会出问题
 * @author CaiDongyu on 2016/4/8.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 表名
	 */
	String tableName();
	/**
	 * 描述
	 */
	String comment();
	/**
	 * 是否自建
	 * 默认是自建，但是，需要全局打开jdbc.auto_create_table参数，才会生效
	 * 如果改成false，那么就算全局打开了jdbc.auto_create_table参数，也不会自建此表
	 */
	boolean autoCreate() default true;
	
	String dataSource() default "";
}
