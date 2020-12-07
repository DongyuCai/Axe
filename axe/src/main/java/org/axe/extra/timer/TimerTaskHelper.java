/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.extra.timer;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.axe.extra.abc_thread.TaskPackBusController;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;

/**
 * Timer 助手 ，类似BeanHelper管理加载所有bean一样，这里是托管Timer
 * 之所以和BeanHelper分开，因为BeanHelper托管了Controller和Service Timer不应该放一起 Created by
 * CaiDongYu on 2020/4/7.
 */
public final class TimerTaskHelper implements Helper {
	private static final Map<String,TimerTask> TIMER_TASK_MAP = new HashMap<>();
	
	@Override
	public void init() throws Exception{
		synchronized (this) {
			Set<Class<?>> timerClassSet = ClassHelper.getClassSetBySuper(TimerTask.class);
				if (CollectionUtil.isNotEmpty(timerClassSet)) {
				for (Class<?> timerClass : timerClassSet) {
					boolean isAbstract = Modifier.isAbstract(timerClass.getModifiers());
					if(isAbstract) continue;
					
					TimerTask timer = ReflectionUtil.newInstance(timerClass);
					
					if(TIMER_TASK_MAP.containsKey(timer.name())){
						throw new Exception("find the same timer name:"+timer.name()+" class:"+timer.getClass()+" === "+TIMER_TASK_MAP.get(timer.name()).getClass());
					}
					TIMER_TASK_MAP.put(timer.name(), timer);
					BeanHelper.setBean(timerClass, timer);
				}
			}
		}
	}

	public static Map<String,TimerTask> getTimerTaskMap() {
		return TIMER_TASK_MAP;
	}

	@Override
	public void onStartUp() throws Exception {
		//系统启动完成后，开始定时任务
		if(CollectionUtil.isNotEmpty(TIMER_TASK_MAP)){
			TaskPackBusController tpBusController = BeanHelper.getBean(TaskPackBusController.class);
			tpBusController.start();
			
			for(TimerTask tt:TIMER_TASK_MAP.values()){
				tpBusController.addTaskPack(tt);
			}
		}
	}

}
