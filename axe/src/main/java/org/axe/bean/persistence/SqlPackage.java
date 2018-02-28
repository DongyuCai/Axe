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

/**
 * Sql 执行需要的 元素包
 * @author CaiDongyu on 2016/5/6.
 */
public class SqlPackage {
	/**
	 * sql语句
	 */
	private String sql;
	/**
	 * 方法参数
	 */
	private Object[] params;
	/**
	 * 参数类型
	 */
	private Class<?>[] paramTypes;
	/**
	 * 语句中?占位符的模式
	 */
	private boolean[] getFlagModeAry;
	
	public SqlPackage(String sql, Object[] params, Class<?>[] paramTypes) {
		super();
		this.sql = sql;
		this.params = params;
		this.paramTypes = paramTypes;
	}
	
	public SqlPackage(String sql, Object[] params, Class<?>[] paramTypes, boolean[] getFlagModeAry) {
		super();
		this.sql = sql;
		this.params = params;
		this.paramTypes = paramTypes;
		this.getFlagModeAry = getFlagModeAry;
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
	
	public Class<?>[] getParamTypes() {
		return paramTypes;
	}
	
	public boolean[] getGetFlagModeAry() {
		return getFlagModeAry;
	}
}
