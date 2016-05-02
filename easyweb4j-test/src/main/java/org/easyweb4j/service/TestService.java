package org.easyweb4j.service;

import org.easyweb4j.annotation.Autowired;
import org.easyweb4j.annotation.Service;
import org.easyweb4j.bean.just4test;
import org.easyweb4j.dao.TestDao;

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
