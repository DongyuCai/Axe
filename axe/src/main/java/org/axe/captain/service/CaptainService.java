package org.axe.captain.service;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Service;
import org.axe.captain.bean.TeamTable;
import org.axe.captain.interface_.Captain;
import org.axe.captain.thread.CaptainMonitorThread;
import org.axe.captain.thread.HeartBeatThread;
import org.axe.util.CollectionUtil;

@Service
public class CaptainService {
	@Autowired
	private Captain captain;
	
	public void setCaptain(Captain captain) {
		this.captain = captain;
	}
	
	
	private CaptainMonitorThread captainMonitorThread = new CaptainMonitorThread();
	private HeartBeatThread beatThread = new HeartBeatThread();
	/**
	 * 启动心跳线程
	 */
	public void startHeartBeatThread() {
		//#通知心跳线程启动
		beatThread.start();
		//#通知监控线程停止
		captainMonitorThread.stop();
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
	
	public Object heartBeat(String captain, String host){
		//##如果Team表还是空的，说明本机是Captain，而且还是第一个人来请求，那么需要把请求来的Captain加到Team表里
		if(CollectionUtil.isEmpty(TeamTable.hosts)){
			synchronized(TeamTable.hosts){
				if(CollectionUtil.isEmpty(TeamTable.hosts)){
					TeamTable.hosts.add(captain);
				}
			}
		}

		//##如果请求的组员不在Team表里，更新进去
		if(!TeamTable.hosts.contains(host)){
			synchronized (TeamTable.hosts) {
				if(!TeamTable.hosts.contains(host)){
					TeamTable.hosts.add(host);
				}
			}
		}
		
		
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

	public Object answerQuestion(String question) {
		if(captain != null){
			return captain.answerQuestion(question);
		}
		return null;
	}
	
}
