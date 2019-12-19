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
package org.axe.helper.mvc;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.Filter;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;

/**
 * Filter 助手 ，类似BeanHelper管理加载所有bean一样，这里是托管Filter
 * 之所以和BeanHelper分开，因为BeanHelper托管了Controller和Service Filter不应该放一起 Created by
 * CaiDongYu on 2016/4/9.
 */
public final class FilterHelper implements Helper {
	private static List<Filter> FILTER_LIST;// 保证顺序

	@Override
	public void init() throws Exception{
		synchronized (this) {
			FILTER_LIST = new ArrayList<>();
			Set<Class<?>> filterClassSet = ClassHelper.getClassSetBySuper(Filter.class);
			List<Filter> filterSortedList = new LinkedList<>();
			if (CollectionUtil.isNotEmpty(filterClassSet)) {
				for (Class<?> filterClass : filterClassSet) {
					boolean isAbstract = Modifier.isAbstract(filterClass.getModifiers());
					if(isAbstract) continue;
					
					Filter filter = ReflectionUtil.newInstance(filterClass);
					filter.init();// 初始化Filter
					
					// 排序比较，按顺序插入到Filter链里
					if (CollectionUtil.isEmpty(filterSortedList)) {
						filterSortedList.add(filter);
					} else {
						int index = 0;
						for (Filter filter_ : filterSortedList) {
							if (filter.setLevel() < filter_.setLevel()) {
								filterSortedList.add(index, filter);
								break;
							}/* else if (filter.setLevel() == filter_.setLevel()){
								throw new Exception("find the same level "+filter.setLevel()+" Filter: "+filterClass+" === "+filter_.getClass());
							} */else {
								index++;
							}
						}
						if (index == filterSortedList.size()) {
							filterSortedList.add(filter);
						}
					}

				}
				FILTER_LIST.addAll(filterSortedList);
			}
		}
	}

	public static List<Filter> getSortedFilterList() {
		return FILTER_LIST;
	}

	@Override
	public void onStartUp() throws Exception {}

}
