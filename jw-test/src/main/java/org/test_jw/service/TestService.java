package org.test_jw.service;

import org.jw.annotation.ioc.Autowired;
import org.jw.annotation.ioc.Service;
import org.test_jw.bean.just4test;
import org.test_jw.dao.TestDao;

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
	
	public Object getAll(){
		return testDao.getAll();
	}
    
}
