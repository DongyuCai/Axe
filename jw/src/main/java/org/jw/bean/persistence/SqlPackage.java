package org.jw.bean.persistence;

/**
 * Sql 执行需要的 元素包
 * Created by CaiDongYu on 2016/5/6.
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
