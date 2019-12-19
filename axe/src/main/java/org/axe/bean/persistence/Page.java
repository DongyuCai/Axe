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
package org.axe.bean.persistence;

import java.util.List;

/**
 * Dao BaseRepostory 分页参数包 
 * @author CaiDongyu on 2016年5月9日 下午1:54:11.
 */
public final class Page<T> {
	/**
	 * 记录
	 */
	private List<T> records;
	
	/**
	 * 分页配置
	 */
	private PageConfig pageConfig;
	
	/**
	 * 全部页加起来的总条数
	 */
	private long count;
	
	/**
	 * 页数
	 */
	private long pages;

	public Page(List<T> records, PageConfig pageConfig, long count, long pages) {
		this.records = records;
		this.pageConfig = pageConfig;
		this.count = count;
		this.pages = pages;
	}

	public List<T> getRecords() {
		return records;
	}

	public PageConfig getPageConfig() {
		return pageConfig;
	}

	public long getCount() {
		return count;
	}

	public long getPages() {
		return pages;
	}
	
}
