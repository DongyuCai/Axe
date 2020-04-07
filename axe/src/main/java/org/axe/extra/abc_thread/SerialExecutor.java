package org.axe.extra.abc_thread;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 串行执行器
 * 这是基于线程的，每个串行执行器，拥有自己的的待处理任务包队列
 * 队列中的任务包，拥有自己的业务及数据，他们顺序固定，必须串行执行
 * 任务包的业务不一定要相同
 */
public final class SerialExecutor extends Thread{
	
	//任务包队列
	private Queue<TaskPack> taskPackQueue = new LinkedList<>();
	
	public int getQueueSize(){
		synchronized (taskPackQueue) {
			return taskPackQueue.size();
		}
	}
	
	/**
	 * 往串行执行器里添加一个任务包，等待执行
	 */
	public void addTaskPack(TaskPack taskPack){
		synchronized (taskPackQueue) {
			taskPackQueue.add(taskPack);
		}
	}
	
	@Override
	public void run() {
		//执行器核心
		//从队列获取并执行任务包
		while(true){
			TaskPack taskPack = taskPackQueue.poll();
			if(taskPack != null){
				try {
					taskPack.task(this);
				} catch (Exception e) {}finally {
					taskPack.setRelease(true);
				}
			}
			try {
				Thread.sleep(10);//TODO 这个休眠可以去掉
			} catch (Exception e) {}
		}
	}
	
}
