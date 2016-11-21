package org.test.service;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Service;
import org.axe.annotation.persistence.Tns;
import org.test.bean.TestTable;
import org.test.dao.TestDao;

/**
 * Created by Administrator on 2016/4/8.
 */
@Service
public class TestService {
	@Autowired
	private TestDao testDao;

	public TestTable get(long id){
		return testDao.getOne(id);	
	}
	
	@Tns
	public Object getAll(){
		return testDao.getAll();
	}
    
}
