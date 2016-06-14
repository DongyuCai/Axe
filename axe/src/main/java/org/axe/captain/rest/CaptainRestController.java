package org.axe.captain.rest;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.captain.service.CaptainService;
import org.axe.constant.RequestMethod;

@Controller(basePath="captain")
public class CaptainRestController {

	@Autowired
	private CaptainService captainService;
	
	@Request(value = "signIn",method = RequestMethod.GET)
	public Object signIn(@RequestParam("host")String host){
		return captainService.signIn(host);
	}
	
	public Object heartBeat(){
		return "";
	}
}
