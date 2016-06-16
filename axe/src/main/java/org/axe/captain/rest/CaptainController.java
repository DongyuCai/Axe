package org.axe.captain.rest;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.captain.bean.TeamTable;
import org.axe.captain.service.CaptainService;
import org.axe.constant.RequestMethod;

@Controller(basePath="captain")
public class CaptainController {

	@Autowired
	private CaptainService captainService;
	
	@Request(value = "signIn",method = RequestMethod.GET)
	public Object signIn(
			@RequestParam("captain")String captain,
			@RequestParam("host")String host){
		return captainService.replySignIn(captain, host);
	}
	

	@Request(value = "monitor",method = RequestMethod.GET)
	public Object monitor(){
		return captainService.replyMonitor();
	}
	
	@Request(value = "heartBeat",method = RequestMethod.GET)
	public Object heartBeat(
			@RequestParam("captain")String captain,
			@RequestParam("host")String host){
		return captainService.heartBeat(captain, host);
	}
	
	@Request(value = "teamTable",method = RequestMethod.GET)
	public Object teamTable(){
		return TeamTable.hosts;
	}
	
	@Request(value = "askQuestion",method = RequestMethod.GET)
	public Object askQuestion(@RequestParam("question")String question){
		return captainService.answerQuestion(question);
	}
}
