package org.axe.captain.helper;

import org.axe.captain.constant.CaptainConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.util.PropsUtil;

/**
 * Captain 配置 助手类
 * Created by CaiDongYu on 2016年6月8日 下午12:54:45.
 */
public final class CaptainConfigHelper{
	
	public static String getAxeCaptainCaptainHost(){
		return PropsUtil.getString(ConfigHelper.getCONFIG_PROPS(), CaptainConfigConstant.AXE_CAPTAIN_CAPTAIN_HOST, null);
	}
	
	public static String getAxeCaptainMyHost(){
		return PropsUtil.getString(ConfigHelper.getCONFIG_PROPS(), CaptainConfigConstant.AXE_CAPTAIN__MY_HOST, null);
	}
}
