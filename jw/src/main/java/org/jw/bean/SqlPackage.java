package org.jw.bean;

/**
 * Sql 执行需要的 元素包
 * Created by CaiDongYu on 2016/5/6.
 */
public class SqlPackage {
	private String sql;
	private Object[] params;
	
	public SqlPackage(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
	
}
