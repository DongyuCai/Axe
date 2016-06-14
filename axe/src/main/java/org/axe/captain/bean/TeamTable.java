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
	
	public static String getCaptain(){
		synchronized (hosts) {
			return CollectionUtil.isEmpty(hosts)?null:hosts.get(0);
		}
	}
}
