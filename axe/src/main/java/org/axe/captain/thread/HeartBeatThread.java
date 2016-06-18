package org.axe.captain.thread;

import org.axe.captain.bean.TeamTable;
import org.axe.captain.helper.CaptainConfigHelper;
import org.axe.captain.helper.CaptainHttpHelper;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳线程
 * Created by CaiDongYu on 2016年6月15日 上午10:20:58.
 */
public final class HeartBeatThread {
	
	private Thread heartBeatThread = null;
	private boolean keep = false;
	
	public void start(){
		if(heartBeatThread == null){
			synchronized (this) {
				final String captain = TeamTable.getCaptain();
				if(heartBeatThread == null && StringUtil.isNotEmpty(captain)){
					heartBeatThread = new Thread("CAPTAIN_HBT"){
						private Logger LOGGER = LoggerFactory.getLogger("CAPTAIN_HBT");
						
						private String generateHeartBeatUrl(String captain, String myHost){
							StringBuilder heartBeat = new StringBuilder(captain);
							if(captain.endsWith("/")){
								heartBeat.append("captain/heartBeat");
							}else{
								heartBeat.append("/captain/heartBeat");
							}
							heartBeat
							.append("?captain=").append(captain)
							.append("&host=").append(myHost);
							return heartBeat.toString();
						}
						
						@Override
						public void run() {
							//#newCaptain表示新任队长，第一次的时候，就是captain
							String newCaptain = captain;
							String myHost = CaptainConfigHelper.getAxeCaptainMyHost();
							keep = true;
							while(keep){
								String heartBeatUrl = this.generateHeartBeatUrl(newCaptain, myHost);
								//#心跳
								try {
									newCaptain = CaptainHttpHelper.askAndRefreshTeamTable(heartBeatUrl);
									if(LOGGER.isInfoEnabled()){
										LOGGER.info("Captain client heat beat success!");
										LOGGER.info("###Team Table START###");
										for(Object host:TeamTable.hosts){
											LOGGER.info(String.valueOf(host));
										}
										LOGGER.info("###Team Table END###");
									}
								} catch (Exception e1) {
									//#心跳失败，更改captain人选
									newCaptain = TeamTable.getCaptain(newCaptain);
									if(StringUtil.isEmpty(newCaptain)){
										//#如果已经到底了，就算了
										break;
									}
									if(newCaptain.equals(myHost)){
										//#如果captain就是自己，那么也没必要再心跳了
										break;
									}
								}
								
								try {
									Thread.sleep(5000);
								} catch (Exception e) {}
							}
							heartBeatThread = null;
						}
					};
					heartBeatThread.start();
				}
			}
		}
	}
	
	public void stop(){
		synchronized (this) {
			if(heartBeatThread != null){
				keep = false;
				while(heartBeatThread.isAlive()){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				heartBeatThread = null;
			}
		}
	}
}
