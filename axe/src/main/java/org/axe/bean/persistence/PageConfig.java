/**
 * MIT License
 * 
 * Copyright (c) 2017 The Axe Project
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

/**
 * Dao 分页配置
 * pageConfig必须放在@Sql注解的Dao方法的最后一个参数位置
 * @author CaiDongyu on 2016年5月9日 下午2:00:00.
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
