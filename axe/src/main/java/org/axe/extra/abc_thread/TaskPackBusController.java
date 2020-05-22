package org.axe.extra.abc_thread;

import java.util.LinkedList;
import java.util.Queue;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Component;
import org.axe.util.LogUtil;

/**
 * 任务包总线
 * 所有的请求，都会先封装成任务包，进入任务包总线
 * 等待总线调度器，自动协商任务包与串行执行器池的协作
 * 如果当前池子繁忙（没有可用于此任务包的执行器）
 * 则此任务包会被插入到总线末端，等待下次询问
 * 
 */
@Component
public final class TaskPackBusController extends Thread{
	
	//任务包总线
	private Queue<TaskPack> taskPackBus = new LinkedList<>();

	public Boolean started = false;
	
	@Override
	public synchronized void start() {
		synchronized (started) {
			if(!started){
				started = true;
				//总线调度启动
				super.start();
			}
		}
	}
	
	public TaskPackBusController() {
		setName("TaskPackBusController");
	}
	
	@Autowired
	private SerialExecutorPool serialExecutorPool;

	public void addTaskPack(TaskPack taskPack) throws Exception{
		if(!taskPack.isRelease()){
			throw new Exception("任务包"+taskPack.getName()+"不可用");
		}
		synchronized (taskPackBus) {
			taskPack.setRelease(false);
			taskPackBus.add(taskPack);
		}
	}
	
	@Override
	public void run() {
		while(true){
			TaskPack pack = null;
			synchronized (taskPackBus) {
				pack = taskPackBus.poll();
			}
			if(pack != null){
				SerialExecutor one = serialExecutorPool.getOne(pack.getName());
				if(one == null){
					//如果没取到执行器，说明池子现在繁忙，那就排到队尾等待
					try {
						//要先更改包状态，否则加不进去
						pack.setRelease(true);
						addTaskPack(pack);
					} catch (Exception e) {
						LogUtil.error(e);
					}
				}else{
					//否则，就交给执行器去等待串行执行了
					one.addTaskPack(pack);
				}
			}
			try {
				Thread.sleep(10);//TODO 可以去掉休眠
			} catch (Exception e) {}
		}
	}
	
}
