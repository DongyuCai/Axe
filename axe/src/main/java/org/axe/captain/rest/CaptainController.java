package org.axe.captain.rest;

import java.util.List;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.captain.bean.TeamTable;
import org.axe.captain.service.CaptainService;
import org.axe.constant.RequestMethod;

//TODO:安全验证
@Controller(basePath="axe-captain")
public class CaptainController {

	@Autowired
	private CaptainService captainService;

	//TODO:移到home
	@Request(value = "teamTable",method = RequestMethod.PUT)
	public Object signIn(
			@RequestParam("host")List<String> host){
		return captainService.resetHosts(host);
	}

	//TODO:移到home
	@Request(value = "captain2man",method = RequestMethod.PUT)
	public Object captain2man(){
		return captainService.captain2man();
	}
	
	//这个接口不移到home页面，是因为需要不通过登陆就访问这个页面的，如果通过登录再访问，就会很麻烦
	@Request(value = "teamTable",method = RequestMethod.GET)
	public Object teamTable(){
		return TeamTable.getTeamTableCopy();
	}
	
	@Request(value = "heartBeat",method = RequestMethod.GET)
	public Object heartBeat(
			@RequestParam("captain")String captain,
			@RequestParam("host")String host){
		return captainService.replyHeartBeat(captain, host);
	}
	
	@Request(value = "monitor",method = RequestMethod.GET)
	public Object monitor(){
		return captainService.replyMonitor();
	}
	
	@Request(value = "askCaptain",method = RequestMethod.GET)
	public Object askCaptain(
			@RequestParam("questionType")String questionType,
			@RequestParam("question")String question){
		return captainService.captainAnswer(questionType, question);
	}

	@Request(value = "askMan",method = RequestMethod.GET)
	public Object askManQuestion(
			@RequestParam("questionType")String questionType,
			@RequestParam("question")String question){
		return captainService.manAnswer(questionType, question);
	}
}
