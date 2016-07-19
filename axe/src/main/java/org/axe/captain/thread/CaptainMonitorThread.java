package org.axe.captain.thread;

import java.util.ArrayList;
import java.util.List;

import org.axe.captain.bean.TeamTable;
import org.axe.util.HttpUtil;

/**
 * Captain 监控心跳线程
 * Created by CaiDongYu on 2016年6月15日 上午10:52:53.
 */
public final class CaptainMonitorThread {

	private Thread captainMonitorThread = null;
	private boolean keep = false;
	
	public void start(final String captain){
		//#启动监控线程
		if(captainMonitorThread == null){
			synchronized (this) {
				captainMonitorThread = new Thread("CAPTAIN_MT"){
					@Override
					public void run() {
						keep = true;
						while(keep){
							//#当只剩下host，其他的都掉线了，就停止吧
							if(TeamTable.isAllDown()){
								break;
							}
							
							//#不停的监听Team表中的host
							List<String> hostsCopy = new ArrayList<>();
							for(String h:TeamTable.getTeamTableCopy()){
								if(captain.equals(h)){
									hostsCopy.add(h);
									continue;
								}
								
								StringBuilder monitor = new StringBuilder(h);
								if(h.endsWith("/")){
									monitor.append("axe-captain/monitor");
								}else{
									monitor.append("/axe-captain/monitor");
								}
								String result;
								try {
									result = HttpUtil.sendGet(monitor.toString());
									if("1".equals(result)){
										//#活着
										hostsCopy.add(h);
									}
								} catch (Exception e) {}
							}
							
							TeamTable.resetHosts(hostsCopy);
							
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						captainMonitorThread = null;
					}
				};
				captainMonitorThread.start();
			}
		}
	}
	
	public void stop(){
		synchronized (this) {
			if(captainMonitorThread != null){
				keep = false;
				while(captainMonitorThread != null){
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
