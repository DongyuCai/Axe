package org.axe.captain.listener;

import java.util.Map;

import org.axe.captain.interface_.Captain;
import org.axe.captain.service.CaptainService;
import org.axe.helper.ioc.BeanHelper;
import org.axe.interface_.mvc.Listener;

public class CaptainListener implements Listener{
	
	private Boolean inited = false;

	@Override
	public void init() throws Exception{
		if(!inited){
			synchronized (inited) {
				if(!inited){
					inited = true;
					//#初始化开发者自我实现的Captain
					Captain captain = null;
					for(Map.Entry<Class<?>, Object> entry:BeanHelper.getBeanMap().entrySet()){
						if(Captain.class.isAssignableFrom(entry.getKey())){
							if(captain == null){
								captain = (Captain)entry.getValue();
							}else{
								throw new Exception("Captain init failed ：find more than 1 Captain.class implement in Bean Map ["
											+captain.getClass().getSimpleName()+"==="
											+entry.getKey().getSimpleName()
											);
							}
						}
						
						
					}
					
					//#尝试注册
					CaptainService captainService = BeanHelper.getBean(CaptainService.class);
					captainService.setCaptain(captain);
					boolean signIn = captainService.signIn();
					if(signIn){
						//#成功注册，开启心跳监控线程
						captainService.startHeartBeatThread();
					}
				}
			}
		}
	}
}
