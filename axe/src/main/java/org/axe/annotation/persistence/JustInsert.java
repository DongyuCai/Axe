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
 * #Dao entity 类属性注解
 * 用来给Entity字段做定义
 * 效果：使用此注解的字段，将无法在updateEntity和saveEntity中被修改到数据库。
 * axe中，saveEntity方法是添加或修改，主要看数据唯一键在表里是否有值，当修改时候，是全字段修改
 * 但是往往存在使用saveEntity时候，只希望修改部分字段，有一些字段是不希望修改的，希望剩余数据保持一致
 * 所以可以使用哪个@JustInsert来注解表明哪些字段，只能通过@Sql语句来修改，用法比如：
 * 	使用@JustInsert标注的字段如下
 * 		@JustInsert
 * 		@Comment("用户积分")
 * 		private int points;
 * 	那么points字段当使用saveEntity方法保存Entity时候，points字段不会被update，只有当@Id字段是空时，points字段会被insert。
 * @author CaiDongyu on 2019/8/21. *完整的3年*
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JustInsert {
	String value();
}
