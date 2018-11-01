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
import org.axe.interface_implement.mvc.AxeRequestParamSetFilter;

/**
 * 不可排除的过滤器
 * 注解用在过滤器上
 * 如果FilterFuckOff未指明排除此注解注释的Filter，则正常不排除，如果指明，则启动报错提示不可排除
 * @author CaiDongyu on 2018/11/1.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UnFuckOff {
	
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
	Class<? extends Filter>[] notFuckOff() default {AxeRequestParamAnalyzeFilter.class,AxeRequestParamSetFilter.class};
}
