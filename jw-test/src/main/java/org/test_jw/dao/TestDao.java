package org.test_jw.dao;

import java.util.List;

import org.jw.annotation.Dao;
import org.jw.annotation.Sql;
import org.jw.bean.Page;
import org.jw.bean.PageConfig;
import org.jw.interface_.BaseRepository;
import org.test_jw.bean.Export;
import org.test_jw.bean.just4test;

@Dao
public interface TestDao extends BaseRepository{

	@Sql("select * from just4test where id = ?")
	public just4test getOne(long id);

	@Sql("select * from just4test")
	public List<just4test> getAll();
	
	@Sql("select * from Export")
	public List<Export> getAllExport();
	
	@Sql("select * from Export where name like ?1")
	public Page<Export> pagingExport(String name,PageConfig pageConfig);
}
