package org.axe.extra.abc_thread;

/**
 * 任务包
 * 把http的一次请求，想象成发起者要服务者做一个事情，给了服务者一个叫做“任务包”的东西
 * 任务包有自己的业务以及携带的数据
 */
public abstract class TaskPack {
	//与executor的name对应
	private String name;

	public TaskPack(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract boolean task(SerialExecutor executor);
	
}
