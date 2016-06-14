package org.axe.captain.service;

import java.util.ArrayList;
import java.util.List;

import org.axe.annotation.ioc.Service;
import org.axe.captain.bean.TeamTable;
import org.axe.captain.constant.CaptainExceptionEnum;
import org.axe.util.HttpUtil;

@Service
public class CaptainService {

	
	public Object signIn(String captain, String host) {
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
		List<String> hostsCopy = new ArrayList<>();
		synchronized (TeamTable.hosts) {
			for(String h:TeamTable.hosts){
				if(h.equals(captain)) {
					//#captain不用监控
					hostsCopy.add(h);
					continue;
				}
				
				if(h.equals(host)) {
					//#当前心跳来者不用监控
					hostsCopy.add(h);
					continue;
				}
				
				//#逐个监控
				StringBuilder monitor = new StringBuilder(h);
				if(h.endsWith("/")){
					monitor.append("captain/monitor");
				}else{
					monitor.append("/captain/monitor");
				}
				String result = HttpUtil.sendGet(monitor.toString());
				if("1".equals(result)){
					//#活着
					hostsCopy.add(h);
				}
			}
			TeamTable.hosts.clear();
			TeamTable.hosts.addAll(hostsCopy);
		}
		return hostsCopy;
	}
	
}
