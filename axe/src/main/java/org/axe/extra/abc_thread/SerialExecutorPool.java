package org.axe.extra.abc_thread;

import java.util.HashMap;
import java.util.Map;

import org.axe.annotation.ioc.Component;

/**
 * 根据名称匹配的串行执行器池
 * 系统启动后，就会初始化一批串行执行器，放入到池子
 * 需要串行执行的请求，会从池子里取空闲的执行器
 */
@Component
public final class SerialExecutorPool {
	//默认的池大小是10个SerialExecutor
	//要修改可以在系统启动的时候，通过Listener来设置此值
	private int maxSize = 10;
	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	private Map<String,SerialExecutor> pool = new HashMap<>();
	
	//线程安全
	public int getPoolSize(){
		synchronized (pool) {
			return pool.size();
		}
	}
	
	//获取一个串行执行器
	//此方法非线程安全，必须只能单一线程使用
	SerialExecutor getOne(String name){
		//线程执行器的名字，是可以改的
		SerialExecutor one = pool.get(name);
		if(one == null){
			//如果池子里没有要的执行器，则查看池子现在是否已满
			if(pool.size() < maxSize){
				//没有满就可以新弄个执行器
				one = new SerialExecutor();
				one.setName(name);
				pool.put(name, one);
				one.start();
			}else{
				//如果满了，就查看一遍，是否有任务包队列已经是空了的执行器
				//如果有，则改名此执行器，用于此任务包的执行
				for(SerialExecutor executor:pool.values()){
					if(executor.getQueueSize() == 0){
						pool.remove(executor.getName());
						executor.setName(name);
						pool.put(name, executor);
						one = executor;
						break;
					}
				}
			}
		}
		return one;
	}
	
}
