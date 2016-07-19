package org.axe.captain.service;

import java.util.List;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Service;
import org.axe.captain.bean.TeamTable;
import org.axe.captain.constant.ThreadModeEnum;
import org.axe.captain.helper.CaptainHelper;
import org.axe.captain.helper.ManHelper;
import org.axe.captain.interface_.Captain;
import org.axe.captain.interface_.Man;
import org.axe.captain.thread.CaptainMonitorThread;
import org.axe.captain.thread.HeartBeatThread;

@Service
public class CaptainService {
	@Autowired
	private CaptainHelper captainHelper;
	@Autowired
	private ManHelper manHelper;
	
	private ThreadModeEnum mode = ThreadModeEnum.AUTO;
	
	private CaptainMonitorThread captainMonitorThread = new CaptainMonitorThread();
	private HeartBeatThread beatThread = new HeartBeatThread();
	/**
	 * 启动心跳线程
	 */
	public void startHeartBeatThread() {
		synchronized (mode) {
			if(mode == ThreadModeEnum.AUTO){
				//#通知心跳线程启动
				beatThread.start();
				//#通知监控线程停止
				captainMonitorThread.stop();
			}
		}
	}
	
	/**
	 * 启动监控线程
	 */
	public void startCaptainMonitorThread(String captain){
		synchronized (mode) {
			if(mode == ThreadModeEnum.AUTO){
				//#通知守护线程
				captainMonitorThread.start(captain);
				//#停止HeartBeat心跳线程
				beatThread.stop();
			}
		}
	}
	
	public Object replyHeartBeat(String captain, String host){
		TeamTable.initCaptain(captain);
		TeamTable.addMan(host);
		
		//#接收到心跳请求的话，说明有人选举本机成为Captain
		startCaptainMonitorThread(captain);
		//#返回Team表
		return TeamTable.getTeamTableCopy();
	}

	/**
	 * 响应Captain监测
	 */
	public String replyMonitor() {
		//#激活下心跳线程
		this.startHeartBeatThread();
		
		return "1";//活着
	}

	public Object captainAnswer(String questionType, String question) {
		Captain captin = captainHelper.getCaptain(questionType);
		if(captin != null){
			return captin.answerQuestion(question);
		}
		return null;
	}
	

	public Object manAnswer(String questionType, String question) {
		Man man = manHelper.getMan(questionType);
		if(man != null){
			return man.answerQuestion(question);
		}
		return null;
	}

	public Object captain2man() {
		synchronized (mode) {
			mode = ThreadModeEnum.AUTO;
		}
		return this.replyMonitor();
	}

	public Object resetHosts(List<String> host) {
		synchronized (mode) {
			mode = ThreadModeEnum.MANUAL;
		}
		//#通知心跳线程停止
		beatThread.stop();
		//#通知监控线程停止
		captainMonitorThread.stop();
		return TeamTable.resetHosts(host);
	}
}
