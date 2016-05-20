package org.jw.helper.base;

import java.util.Date;

/**
 * 框架自生状态 助手类
 * Created by CaiDongYu on 2016年5月20日 上午8:43:39.
 */
public final class FrameworkStatusHelper {
	
	/**
	 * 框架启动时间
	 */
	private static final Date STARTUP_TIME;
	
	
	static{
		STARTUP_TIME = new Date();
	}


	
	public static Date getStartupTime() {
		return STARTUP_TIME;
	}
}
