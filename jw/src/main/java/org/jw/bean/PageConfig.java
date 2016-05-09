package org.jw.bean;

/**
 * Dao 分页配置
 * pageConfig必须放在@Sql注解的Dao方法的最后一个参数位置
 * Created by CaiDongYu on 2016年5月9日 下午2:00:00.
 */
public class PageConfig {

	/**
	 * 当前页码
	 */
	private long pageNum;
	/**
	 * 每页大小
	 */
	private long pageSize;
	/**
	 * mysql limit 起始行
	 */
	private long limitParam1;
	/**
	 * mysql limit 查询条数
	 */
	private long limitParam2;

	
	public PageConfig(long pageNum, long pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		init();
	}
	
	private void init(){
		this.pageNum = this.pageNum < 1 ? 1:this.pageNum;
		this.pageSize = this.pageSize < 1 ? 1:this.pageSize;
		this.limitParam1 = (this.pageNum-1)*this.pageSize;
		this.limitParam2 = this.pageSize;
	}

	public long getPageNum() {
		return pageNum;
	}

	public long getPageSize() {
		return pageSize;
	}

	public long getLimitParam1() {
		return limitParam1;
	}

	public long getLimitParam2() {
		return limitParam2;
	}
}
