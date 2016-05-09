package org.jw.bean;

import java.util.List;

/**
 * Dao BaseRepostory 分页参数包 
 * Created by CaiDongYu on 2016年5月9日 下午1:54:11.
 */
public class Page<T> {
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
