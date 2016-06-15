package org.axe.captain.service;

import java.util.ArrayList;
import java.util.List;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Service;
import org.axe.captain.bean.TeamTable;
import org.axe.captain.constant.CaptainExceptionEnum;
import org.axe.captain.helper.CaptainConfigHelper;
import org.axe.captain.helper.CaptainHttpHelper;
import org.axe.captain.interface_.Captain;
import org.axe.captain.thread.CaptainMonitorThread;
import org.axe.captain.thread.HeartBeatThread;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CaptainService {
	private Logger LOGGER = LoggerFactory.getLogger(CaptainService.class);

	@Autowired
	private Captain captain;
	
	
	private CaptainMonitorThread captainMonitorThread = new CaptainMonitorThread();
	private HeartBeatThread beatThread = new HeartBeatThread();


	public boolean signIn() throws Exception {
		String captain = CaptainConfigHelper.getAxeCaptainCaptainHost();
		if(StringUtil.isNotEmpty(captain)){
			String myHost = CaptainConfigHelper.getAxeCaptainMyHost();
			if(StringUtil.isNotEmpty(myHost)){
				
				StringBuilder signIn = new StringBuilder(captain);
				if(captain.endsWith("/")){
					signIn.append("captain/signIn");
				}else{
					signIn.append("/captain/signIn");
				}
				signIn
				.append("?captain=").append(captain)
				.append("&host=").append(myHost);
				
				CaptainHttpHelper.askAndRefreshTeamTable(signIn.toString());
				
				if(LOGGER.isInfoEnabled()){
					LOGGER.info("Captain client signIn success!");
					LOGGER.info("###Team Table START###");
					for(Object host:TeamTable.hosts){
						LOGGER.info(String.valueOf(host));
					}
					LOGGER.info("###Team Table END###");
				}
				
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 启动心跳线程
	 */
	public void startHeartBeatThread() {
		//#通知心跳线程启动
		beatThread.start();
		//#通知监控线程停止
		
	}
	
	/**
	 * 启动监控线程
	 */
	public void startCaptainMonitorThread(String captain){
		//#通知守护线程
		captainMonitorThread.start(captain);
		//#停止HeartBeat心跳线程
		beatThread.stop();
	}
	
	/**
	 * 响应组员启动注册
	 */
	public Object replySignIn(String captain, String host) {
		//#注册
		List<String> hostsCopy = new ArrayList<>();
		hostsCopy.add(captain);
		synchronized (TeamTable.hosts) {
			//##是否已存在
			for(String h:TeamTable.hosts){
				if(h.equals(captain)) {
					//##跳过captain
					continue;
				}
				if(h.equals(host)){
					return CaptainExceptionEnum.HOST_EXISTED.code;
				}
				hostsCopy.add(h);
			}
			hostsCopy.add(host);
			TeamTable.hosts.clear();
			TeamTable.hosts.addAll(hostsCopy);
		}
		return hostsCopy;
	}
	
	public Object heartBeat(String captain, String host){
		//#接收到心跳请求的话，说明有人选举本机成为Captain
		startCaptainMonitorThread(captain);
		//#返回Team表
		return TeamTable.hosts;
	}

	/**
	 * 响应Captain监测
	 */
	public String replyMonitor() {
		//#激活下心跳线程
		this.startHeartBeatThread();
		
		return "1";//活着
	}

	public Object answerQuestion(String question) {
		if(captain != null){
			return captain.answerQuestion(question);
		}
		return null;
	}
	
}
