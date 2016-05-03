package org.jw.dao;

import java.util.List;

import org.jw.annotation.Dao;
import org.jw.annotation.Sql;
import org.jw.bean.just4test;

@Dao
public interface TestDao {

	@Sql("select * from just4test where id = ?")
	public just4test getOne(long id);

	@Sql("select * from just4test")
	public List<just4test> getAll();
}
