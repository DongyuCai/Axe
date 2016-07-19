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
	private static List<String> hosts = new ArrayList<>();
	public static String myHost = null;
	
	//#初始化 captain和myHost
	static{
		String axeCaptainCaptainHost = CaptainConfigHelper.getAxeCaptainCaptainHost();
		myHost = CaptainConfigHelper.getAxeCaptainMyHost();
		if(StringUtil.isNotEmpty(axeCaptainCaptainHost) && StringUtil.isNotEmpty(myHost)){
			hosts.add(axeCaptainCaptainHost);
			hosts.add(myHost);//#这个第二的位置，将来被Captain同步后可能就不是自己了
		}
	}
	
	/**
	 * 取得Team表的复印件
	 */
	public static List<String> getTeamTableCopy() {
		List<String> hostsCopy = new ArrayList<>();
		synchronized (hosts) {
			hostsCopy.addAll(hosts);
		}
		return hostsCopy;
	}
	
	/**
	 * 取得Team表中，除了Captain以外的队员列表复印件
	 */
	public static List<String> getMansCopy() {
		List<String> hostsCopy = new ArrayList<>();
		synchronized (hosts) {
			if(CollectionUtil.isNotEmpty(hosts)){
				hostsCopy.addAll(hosts.subList(1, hosts.size()));
			}
		}
		return hostsCopy;
	}
	
	/**
	 * 取得Captain
	 */
	public static String getCaptain(){
		synchronized (hosts) {
			return CollectionUtil.isEmpty(hosts)?null:hosts.get(0);
		}
	}
	
	/**
	 * 取得下一位Captain
	 */
	public static String getNextCaptain(String currentCaptain){
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

	/**
	 * 重置Team表
	 */
	public static Object resetHosts(List<String> host) {
		synchronized (hosts) {
			hosts.clear();
			if(CollectionUtil.isNotEmpty(host)){
				hosts.addAll(host);
			}
		}
		return getTeamTableCopy();
	}
	
	/**
	 * 第一次当hosts为空的时候，添加captain
	 */
	public static void initCaptain(String captain){
		//##如果Team表还是空的，说明本机是Captain，而且还是第一个人来请求，那么需要把请求来的Captain加到Team表里
		if(CollectionUtil.isEmpty(hosts)){
			synchronized(hosts){
				if(CollectionUtil.isEmpty(hosts)){
					hosts.add(captain);
				}
			}
		}
	}
	
	/**
	 * 添加组员
	 */
	public static void addMan(String host){
		//##如果请求的组员不在Team表里，更新进去
		if(!hosts.contains(host)){
			synchronized (hosts) {
				if(!hosts.contains(host)){
					hosts.add(host);
				}
			}
		}
	}
	
	/**
	 * 所机器有都掉线了
	 */
	public static boolean isAllDown(){
		synchronized (hosts) {
			if(CollectionUtil.isEmpty(hosts) || hosts.size() <= 1){
				//#当只剩下host，其他的都掉线了，就停止吧
				return true;
			}
		}
		return false;
	}
}
