package org.test.rest;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.constant.RequestMethod;

@Controller(basePath="test-login")
public class TestLoginController {

	
	@Request(value="login",method=RequestMethod.POST)
	public Object login(@RequestParam("username")String username,
				@RequestParam("password")String password){
		return "TOKEN_TEST_123";
	}
	
	
}
