package com.axe.first_project.rest;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.constant.RequestMethod;

@Controller(basePath="/hello_word",desc="HelloWord")
public class TestRest {
	
	@Request(path="/first",method=RequestMethod.GET,desc="第一个接口")
	public String first(
		@RequestParam(name="name",required=true,desc="姓名")String name,
		@RequestParam(name="age",desc="年龄")Integer age
			){
		return "姓名:"+name+" 年龄:"+(age == null?"不知道":age);
	}
	
}