package org.axe.captain.listener;

import java.util.Set;

import org.axe.captain.helper.CaptainHelper;
import org.axe.captain.helper.ManHelper;
import org.axe.captain.interface_.Captain;
import org.axe.captain.interface_.Man;
import org.axe.captain.service.CaptainService;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.mvc.Listener;
import org.axe.util.ReflectionUtil;

public class CaptainListener implements Listener{
	
	private Boolean inited = false;

	@Override
	public void init() throws Exception{
		if(!inited){
			synchronized (inited) {
				if(!inited){
					inited = true;
					//#初始化开发者自我实现的Captain和Man
					CaptainHelper captainHelper = BeanHelper.getBean(CaptainHelper.class);
					Set<Class<?>> captainClassSet = ClassHelper.getClassSetBySuper(Captain.class);
					for(Class<?> captainClass:captainClassSet){
						Captain	captain = ReflectionUtil.newInstance(captainClass);
						if(captainHelper.captainExists(captain.accpetQuestionType())){
							throw new Exception("CaptainHelper init failed ：find tow Captain.class implement for questionType ["+captain.accpetQuestionType()+"]["
									+captainHelper.getCaptain(captain.accpetQuestionType()).getClass().getSimpleName()+"==="
									+captainClass.getSimpleName()
									);
						}else{
							captainHelper.addCaptain(captain);
						}
					}
					ManHelper manHelper = BeanHelper.getBean(ManHelper.class);
					Set<Class<?>> manClassSet = ClassHelper.getClassSetBySuper(Man.class);
					for(Class<?> manClass:manClassSet){
						Man	man = ReflectionUtil.newInstance(manClass);
						if(manHelper.manExists(man.accpetQuestionType())){
							throw new Exception("ManHelper init failed ：find tow Man.class implement for questionType ["+man.accpetQuestionType()+"]["
									+manHelper.getMan(man.accpetQuestionType()).getClass().getSimpleName()+"==="
									+manClass.getSimpleName()
									);
						}else{
							manHelper.addMan(man);
						}
					}
					
					//#开启心跳监控线程
					CaptainService captainService = BeanHelper.getBean(CaptainService.class);
					captainService.startHeartBeatThread();
				}
			}
		}
	}
}
