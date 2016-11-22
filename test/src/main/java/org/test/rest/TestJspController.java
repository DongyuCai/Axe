package org.test.rest;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Request;
import org.axe.bean.mvc.View;
import org.axe.constant.RequestMethod;

@Controller(basePath="jsp")
public class TestJspController {

	@FilterFuckOff
	@Request(value="server",method=RequestMethod.GET)
	public View redirectToJsp1(){
		
		return new View("index.jsp");
	}
	

	@FilterFuckOff
	@Request(value="browser",method=RequestMethod.GET)
	public View redirectToJsp2(){
		
		return new View("/index.jsp");
	}

}
