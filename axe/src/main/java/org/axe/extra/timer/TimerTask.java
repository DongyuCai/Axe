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

import org.axe.extra.abc_thread.SerialExecutor;
import org.axe.extra.abc_thread.TaskPack;

/**
 * 定时器接口
 * @author CaiDongYu on 2020/12/7.
 */
public abstract class TimerTask extends TaskPack{
	//最后一次执行时间
	private long lastExecuteTime;
	
	public TimerTask() {
		super(null);
		setName(name());
	}

	private TimerTask(String name) {
		super(name);
	}
	
	@Override
	public boolean task(SerialExecutor executor) {
		long currentTime = System.currentTimeMillis();
		if(currentTime-lastExecuteTime>timeSec()*1000){
			lastExecuteTime = currentTime;
			try {
				doSomething();
			} catch (Exception e) {}
		}
		return true;//因为是定时任务，所以会循环执行
	}
	
	/**
	 * 定时器名称
	 * @return
	 */
	public abstract String name();

	/**
	 * @定时秒数
	 */
	public abstract int timeSec();
	
	/**
	 * 如果可以执行，那就做点什么
	 */
	public abstract void doSomething();
	
}
