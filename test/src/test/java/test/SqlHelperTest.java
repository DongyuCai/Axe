package test;

import org.axe.Axe;
import org.axe.helper.persistence.SqlHelper;

public class SqlHelperTest {
	public static void main(String[] args) {
		try {
			Axe.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sql = "select t.* from Account t where t.id = ?1 #2";
		String append = "and lastLoginIp = 'a'";
		Object[] params = {1,append};
		sql = SqlHelper.convertSqlAppendCommand(sql, params);
		sql = SqlHelper.convertHql2Sql(sql);
		System.out.println(sql);
	}
}
