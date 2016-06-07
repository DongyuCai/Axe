package org.axe.helper.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.Listener;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;

/**
 * Listener 启动助手类
 * Created by CaiDongYu on 2016年6月7日 下午1:35:37.
 */
public final class ListenerHelper implements Helper{
	
	private static List<Listener> LISTENER_LIST;
	
	
	@Override
	public void init() {
		synchronized (this) {
			LISTENER_LIST = new ArrayList<>();
			Set<Class<?>> classSet = ClassHelper.getClassSetBySuper(Listener.class);
			if(CollectionUtil.isNotEmpty(classSet)){
				for(Class<?> listenerClass:classSet){
					Listener listener = ReflectionUtil.newInstance(listenerClass);
					listener.init();//初始化
					LISTENER_LIST.add(listener);
				}
			}
		}
	}

}
