package org.axe.captain.bean;

import java.util.ArrayList;
import java.util.List;

import org.axe.util.CollectionUtil;

/**
 * Team 表，存放host
 * Created by CaiDongYu on 2016年6月14日 下午5:24:11.
 */
public final class TeamTable {
	public static List<String> hosts = new ArrayList<>();
	
	public static List<String> getTeamTableCopy() {
		List<String> hostsCopy = new ArrayList<>();
		synchronized (hosts) {
			hostsCopy.addAll(hosts);
		}
		return hostsCopy;
	}
	
	public static List<String> getMansCopy() {
		List<String> hostsCopy = new ArrayList<>();
		synchronized (hosts) {
			if(CollectionUtil.isNotEmpty(hosts)){
				hostsCopy.addAll(hosts.subList(1, hosts.size()));
			}
		}
		return hostsCopy;
	}
	
	public static String getCaptain(){
		synchronized (hosts) {
			return CollectionUtil.isEmpty(hosts)?null:hosts.get(0);
		}
	}
	
	public static String getCaptain(String currentCaptain){
		String captain = null;
		synchronized (hosts) {
			boolean findCurrentCaptain = false;
			for(String h:hosts){
				if(h.equals(currentCaptain)){
					findCurrentCaptain = true;
					continue;
				}
				//#currentCaptain的下一个，作为目标Captain人选
				if(findCurrentCaptain){
					captain = h;
					break;
				}
			}
		}
		return captain;
	}
}
