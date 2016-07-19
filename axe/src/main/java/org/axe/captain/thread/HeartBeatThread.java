package org.axe.captain.thread;

import java.util.ArrayList;
import java.util.List;

import org.axe.captain.bean.TeamTable;
import org.axe.captain.constant.CaptainExceptionEnum;
import org.axe.captain.helper.CaptainConfigHelper;
import org.axe.util.CollectionUtil;
import org.axe.util.HttpUtil;
import org.axe.util.JsonUtil;
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
								heartBeat.append("axe-captain/heartBeat");
							}else{
								heartBeat.append("/axe-captain/heartBeat");
							}
							heartBeat
							.append("?captain=").append(captain)
							.append("&host=").append(myHost);
							return heartBeat.toString();
						}
						
						public String doHearBeat(String url) throws Exception{
							String result = HttpUtil.sendGet(url);
							CaptainExceptionEnum exception = CaptainExceptionEnum.getException(result);
							if(exception != null){
								throw new Exception("Captain heartBeat failed ："+exception.desc);
							}
							try {
								if(StringUtil.isEmpty(result)){
									throw new Exception("http response result is empty");
								}
								//#新表
								List<?> newHosts = JsonUtil.fromJson(result,ArrayList.class);
								List<String> hosts = new ArrayList<>();
								if(CollectionUtil.isNotEmpty(newHosts)){
									for(Object obj:newHosts){
										hosts.add(String.valueOf(obj));
									}
								}
								TeamTable.resetHosts(hosts);
								return CollectionUtil.isNotEmpty(hosts)?hosts.get(0):null;
							} catch (Throwable e) {
								throw new Exception("Captain http failed ："+result);
							}
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
									newCaptain = this.doHearBeat(heartBeatUrl);
									if(LOGGER.isInfoEnabled()){
										LOGGER.info("Captain client heat beat success!");
										LOGGER.info("###Team Table START###");
										for(Object host:TeamTable.getTeamTableCopy()){
											LOGGER.info(String.valueOf(host));
										}
										LOGGER.info("###Team Table END###");
									}
								} catch (Exception e1) {
									//#心跳失败，更改captain人选
									newCaptain = TeamTable.getNextCaptain(newCaptain);
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
				while(heartBeatThread != null){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
