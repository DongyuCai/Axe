package org.axe.helper.base;

import java.util.Date;

import org.axe.helper.Helper;

/**
 * 框架自生状态 助手类
 * Created by CaiDongYu on 2016年5月20日 上午8:43:39.
 */
public final class FrameworkStatusHelper implements Helper{
	
	/**
	 * 框架启动时间
	 */
	private static Date STARTUP_TIME;
	
	@Override
	public void init() {
		synchronized (this) {
			STARTUP_TIME = new Date();
		}
	}
	
	public static Date getStartupTime() {
		return STARTUP_TIME;
	}
}
