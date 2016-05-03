package org.jw.service;

import org.jw.annotation.Autowired;
import org.jw.annotation.Service;
import org.jw.bean.just4test;
import org.jw.dao.TestDao;

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
