package org.test.service;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Service;
import org.axe.annotation.persistence.Tns;
import org.axe.hoke.annotation.Hoke;
import org.test.bean.just4test;
import org.test.dao.TestDao;

/**
 * Created by Administrator on 2016/4/8.
 */
@Service
public class TestService {
	@Autowired
	private TestDao testDao;

	public just4test get(long id){
		return testDao.getOne(id);	
	}
	
	@Tns
	public Object getAll(){
		return testDao.getAll();
	}
    
}
