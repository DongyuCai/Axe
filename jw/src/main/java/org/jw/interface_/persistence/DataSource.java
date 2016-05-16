package org.jw.interface_.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库数据源
 * 扩展持久层数据源，需要实现这个接口
 * 然后在jw.properties里配置jdbc.datasource即可
 * Created by CaiDongYu on 2016年5月16日 上午11:03:51.
 */
public interface DataSource {

	public Connection getConnection() throws SQLException ;
}
