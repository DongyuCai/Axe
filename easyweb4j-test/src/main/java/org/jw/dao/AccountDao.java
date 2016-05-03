package org.jw.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jw.annotation.Dao;
import org.jw.annotation.Sql;
import org.jw.bean.Account;

@Dao
public interface AccountDao {
	
	@Sql("select * from iot_user_account")
	public List<Account> getAll();

	@Sql("select * from iot_user_account limit 1")
	public Account getLimit1();
	
	@Sql("select count(*) 'all' from iot_user_account")
	public Map<String,Double> getCount();
	
	@Sql("select * from iot_user_account limit 1")
	public Map<String,Object> getMap();
	

	@Sql("select regeist_date from iot_user_account where id=?")
	public Date getString(long id);
}
