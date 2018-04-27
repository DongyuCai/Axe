package org.test.rest;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Request;
import org.axe.constant.RequestMethod;
import org.test.bean.Account;
import org.test.dao.TestDao;
import org.test.service.TestService;

/**
 * Created by Administrator on 2016/4/8.
 */
@FilterFuckOff
@Controller(basePath = "test")
public class TestEntityController {
	
	@Autowired
	private TestService testService;
	
	@Autowired
	private TestDao testDao;
	
	@Request(value="/entity",method=RequestMethod.GET)
	public Object testSign(Account account){
		return "ok";
	}
	
}
