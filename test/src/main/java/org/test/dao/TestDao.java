package org.test.dao;

import java.util.List;

import org.axe.annotation.persistence.Dao;
import org.axe.annotation.persistence.Sql;
import org.axe.bean.persistence.Page;
import org.axe.bean.persistence.PageConfig;
import org.axe.interface_.persistence.BaseRepository;
import org.test.bean.Account;
import org.test.bean.Export;
import org.test.bean.TestTable;

@Dao
public interface TestDao extends BaseRepository{

	@Sql("select * from TestTable where id = ?")
	public TestTable getOne(long id);

	@Sql("select * from TestTable")
	public List<TestTable> getAll();
	
	@Sql("select * from TestTable where name like '%test%'")
	public Page<TestTable> page();
	
	@Sql("#1")
	public List<Account> getAccountList(String sql);
	
	@Sql("select * from Export")
	public List<Export> getAllExport();
	
	@Sql("select * from Export where name like ?1")
	public Page<Export> pagingExport(String name,PageConfig pageConfig);
}
