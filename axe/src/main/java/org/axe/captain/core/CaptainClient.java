package org.axe.captain.core;

import java.util.ArrayList;
import java.util.List;

import org.axe.captain.bean.TeamTable;
import org.axe.captain.constant.CaptainExceptionEnum;
import org.axe.captain.helper.CaptainConfigHelper;
import org.axe.util.HttpUtil;
import org.axe.util.JsonUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队长模式的客户端
 * Created by CaiDongYu on 2016年6月14日 下午4:03:58.
 */
public final class CaptainClient {
	private Logger LOGGER = LoggerFactory.getLogger(CaptainClient.class);
	
	private Thread heartBeatThread = null;

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
				
				askAndRefreshTeamTable(signIn.toString());
				
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

	
	public void startHeartBeatThread() {
		synchronized (this) {
			if(heartBeatThread == null){
				heartBeatThread = new Thread("CAPTAIN_HBT"){
					private Logger LOGGER = LoggerFactory.getLogger("CAPTAIN_HBT");
					
					@Override
					public void run() {
						String captain = TeamTable.getCaptain();
						String myHost = CaptainConfigHelper.getAxeCaptainMyHost();
						StringBuilder heartBeat = new StringBuilder(captain);
						if(captain.endsWith("/")){
							heartBeat.append("captain/heartBeat");
						}else{
							heartBeat.append("/captain/heartBeat");
						}
						heartBeat
						.append("?captain=").append(captain)
						.append("&host=").append(myHost);
						while(true){
							//#心跳
							try {
								askAndRefreshTeamTable(heartBeat.toString());
								if(LOGGER.isInfoEnabled()){
									LOGGER.info("Captain client heat beat success!");
									LOGGER.info("###Team Table START###");
									for(Object host:TeamTable.hosts){
										LOGGER.info(String.valueOf(host));
									}
									LOGGER.info("###Team Table END###");
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							
							try {
								Thread.sleep(10000);
							} catch (Exception e) {}
						}
					}
				};
				heartBeatThread.start();
			}
		}
	}
	
	
	private void askAndRefreshTeamTable(String url) throws Exception{
		String result = HttpUtil.sendGet(url);
		CaptainExceptionEnum exception = CaptainExceptionEnum.getException(result);
		if(exception != null){
			throw new Exception("Captain start failed ："+exception.desc);
		}
		try {
			//#新表
			List<?> newHosts = JsonUtil.fromJson(result,ArrayList.class);
			synchronized (TeamTable.hosts) {
				TeamTable.hosts.clear();
				for(Object obj:newHosts){
					TeamTable.hosts.add(String.valueOf(obj));
				}
			}
		} catch (Throwable e) {
			throw new Exception("Captain start failed ："+result);
		}
	}
}
