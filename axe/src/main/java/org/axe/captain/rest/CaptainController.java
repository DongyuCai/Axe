package org.axe.captain.rest;

import java.util.List;

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
	
	@Request(value = "heartBeat",method = RequestMethod.GET)
	public Object heartBeat(
			@RequestParam("captain")String captain,
			@RequestParam("host")String host){
		return captainService.heartBeat(captain, host);
	}
	
	@Request(value = "monitor",method = RequestMethod.GET)
	public Object monitor(){
		return captainService.replyMonitor();
	}
	
	@Request(value = "teamTable",method = RequestMethod.GET)
	public Object teamTable(){
		return TeamTable.hosts;
	}
	
	@Request(value = "teamTable",method = RequestMethod.PUT)
	public Object signIn(
			@RequestParam("host")List<String> host){
		return TeamTable.resetHosts(host);
	}
	
	@Request(value = "askQuestion",method = RequestMethod.GET)
	public Object askQuestion(
			@RequestParam("questionType")String questionType,
			@RequestParam("question")String question){
		return captainService.answerQuestion(questionType, question);
	}
}
