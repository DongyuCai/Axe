package org.axe.captain.listener;

import org.axe.captain.core.CaptainClient;
import org.axe.interface_.mvc.Listener;

public class CaptainListener implements Listener{
	
	private boolean inited = false;

	@Override
	public void init() throws Exception{
		if(!inited){
			//#启动Captain模式的入口
			CaptainClient captainClient = new CaptainClient();
			boolean signIn = captainClient.signIn();
			if(signIn){
				//#成功注册，开启心跳监控线程
				
			}
		}
	}
}
