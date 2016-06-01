package org.test.dao;

import java.util.List;

import org.axe.annotation.persistence.Dao;
import org.axe.annotation.persistence.Sql;
import org.axe.bean.persistence.Page;
import org.axe.bean.persistence.PageConfig;
import org.axe.interface_.persistence.BaseRepository;
import org.test.bean.Export;
import org.test.bean.just4test;

@Dao
public interface TestDao extends BaseRepository{

	@Sql("select * from just4test where id = ?")
	public just4test getOne(long id);

	@Sql("select * from just4test")
	public List<just4test> getAll();
	
	@Sql("select * from just4test where name like '%test%'")
	public Page<just4test> page();
	
	@Sql("select * from Export")
	public List<Export> getAllExport();
	
	@Sql("select * from Export where name like ?1")
	public Page<Export> pagingExport(String name,PageConfig pageConfig);
}
