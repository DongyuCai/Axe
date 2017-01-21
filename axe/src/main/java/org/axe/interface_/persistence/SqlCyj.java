package org.axe.interface_.persistence;

import org.axe.bean.persistence.SqlPackage;
import org.axe.helper.persistence.DataBaseHelper;

/**
 * Sql程咬金
 * 这个程咬金，是专门半路杀出，拦截Sql的
 * 地点在jdbc把Sql提交给数据库的最后一站
 * {@link DataBaseHelper#getPrepareStatement}
 */
public interface SqlCyj {

	/**
	 * 抢劫SqlPackage包
	 * @param sp
	 * @return 归还SqlPackage包
	 */
	SqlPackage robSqlPackage(SqlPackage sp);
}
