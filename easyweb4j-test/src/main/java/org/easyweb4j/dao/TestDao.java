package org.easyweb4j.dao;

import java.util.List;

import org.easyweb4j.annotation.Dao;
import org.easyweb4j.annotation.Sql;
import org.easyweb4j.bean.just4test;

@Dao
public interface TestDao {

	@Sql("select * from just4test where id = ?")
	public just4test getOne(long id);

	@Sql("select * from just4test")
	public just4test[] getAll();
}
