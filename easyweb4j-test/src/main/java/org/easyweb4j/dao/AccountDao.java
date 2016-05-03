package org.easyweb4j.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.easyweb4j.annotation.Dao;
import org.easyweb4j.annotation.Sql;
import org.easyweb4j.bean.Account;

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
