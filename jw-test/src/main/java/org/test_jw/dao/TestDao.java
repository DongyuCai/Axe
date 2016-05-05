package org.test_jw.dao;

import java.util.List;

import org.jw.annotation.Dao;
import org.jw.annotation.Sql;
import org.jw.interface_.Repository;
import org.test_jw.bean.just4test;

@Dao
public interface TestDao extends Repository{

	@Sql("select * from just4test where id = ?")
	public just4test getOne(long id);

	@Sql("select * from just4test")
	public List<just4test> getAll();
}
