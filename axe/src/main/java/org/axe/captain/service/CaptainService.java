package org.axe.captain.service;

import java.util.ArrayList;
import java.util.List;

import org.axe.annotation.ioc.Service;
import org.axe.captain.constant.CaptainExceptionEnum;

@Service
public class CaptainService {

	private List<String> hosts = new ArrayList<>();

	public Object signIn(String host) {
		List<String> hostsCopy = new ArrayList<>();
		synchronized (hosts) {
			//#是否已存在
			for(String h:hosts){
				if(h.equals(host)){
					return CaptainExceptionEnum.HOST_EXISTED.code;
				}
				hostsCopy.add(h);
			}
			hosts.add(host);
			hostsCopy.add(host);
		}
		
		return hostsCopy;
	}
	
	
}
