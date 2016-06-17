package org.axe.captain.bean;

import java.util.ArrayList;
import java.util.List;

import org.axe.captain.helper.CaptainConfigHelper;
import org.axe.util.CollectionUtil;
import org.axe.util.StringUtil;

/**
 * Team 表，存放host
 * Created by CaiDongYu on 2016年6月14日 下午5:24:11.
 */
public final class TeamTable {
	public static List<String> hosts = new ArrayList<>();
	public static String myHost = null;
	static{
		String axeCaptainCaptainHost = CaptainConfigHelper.getAxeCaptainCaptainHost();
		myHost = CaptainConfigHelper.getAxeCaptainMyHost();
		if(StringUtil.isNotEmpty(axeCaptainCaptainHost) && StringUtil.isNotEmpty(myHost)){
			hosts.add(axeCaptainCaptainHost);
			hosts.add(myHost);//#这个第二的位置，将来被Captain同步后可能就不是自己了
		}
	}
	
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

	public static Object resetHosts(List<String> host) {
		if(CollectionUtil.isNotEmpty(host)){
			synchronized (hosts) {
				hosts.clear();
				hosts.addAll(host);
			}
		}
		return getTeamTableCopy();
	}
}
