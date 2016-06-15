package org.axe.captain.rest;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.captain.service.ManService;
import org.axe.constant.RequestMethod;

@Controller(basePath="captain/man")
public class ManRestController {

	@Autowired
	private ManService manService;
	
	@Request(value = "askQuestion",method = RequestMethod.GET)
	public Object askQuestion(@RequestParam("question")String question){
		return manService.answerQuestion(question);
	}
}
