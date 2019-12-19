package org.axe.bean.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.axe.util.sql.CommonSqlUtil;

/**
 * Sql 执行类，包装了PrepareStatement
 * @author CaiDongyu on 2019/2/13.
 */
public final class SqlExecutor {

	private String dataSourceName;
	private SqlPackage sp;
	private PreparedStatement ps;
	
	public SqlExecutor(String dataSourceName,SqlPackage sp,PreparedStatement ps) {
		this.dataSourceName = dataSourceName;
		this.sp = sp;
		this.ps = ps;
	}

	/**
	 * 准备执行 PreparedStatement
	 */
	public PreparedStatement readyExecuteStatement() {
		//调试打印sql
		CommonSqlUtil.debugSql(dataSourceName,sp);
		return ps;
	}
	
	public Object getGeneratedKeys() throws SQLException{
		Object generatedKey = null;
		ResultSet rs = ps.getGeneratedKeys();
    	if(rs.next()){
    		generatedKey = rs.getObject(1);
    	}
    	rs.close();
    	return generatedKey;
	}
	
	public void close() throws SQLException{
		ps.close();
	}
	
}
