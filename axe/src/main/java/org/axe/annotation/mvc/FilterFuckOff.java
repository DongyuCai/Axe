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
package org.axe.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axe.interface_.mvc.Filter;
import org.axe.interface_implement.mvc.AxeRequestParamAnalyzeFilter;

/**
 * 排除过滤器注解
 * 应用在那些不需要某些过滤器的Controller方法上
 * 也可以直接暴力加在Controller上，这样方法上的此注解会失效，
 * Controller中所有方法都依照Controller上的此注解配置
 * @author CaiDongyu on 2016/4/8.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterFuckOff {
	
	/**
	 * 需要排除的过滤器列表
	 * 默认为空，注意注意，为空不是说不排除，而是排除所有！
	 */
	Class<? extends Filter>[] value() default {};
	
	/**
	 * 不包括的过滤器列表
	 * 如果与排除项冲突，从排除项中扣除
	 * 默认不包括 AxeRequestParamAnalyzeFilter
	 */
	Class<? extends Filter>[] notFuckOff() default {AxeRequestParamAnalyzeFilter.class};
}
