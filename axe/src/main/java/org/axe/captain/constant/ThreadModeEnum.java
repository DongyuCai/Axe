package org.axe.captain.constant;

/**
 * 守护线程的模式
 * 自动维护、手动维护
 * Created by CaiDongYu on 2016年6月8日 上午10:43:47.
 */
public enum ThreadModeEnum {
	AUTO(1,"自动"),
	MANUAL(2,"手动");
	
	public int mode;
	public String desc;
	
	private ThreadModeEnum(int mode, String desc) {
		this.mode = mode;
		this.desc = desc;
	}
}
