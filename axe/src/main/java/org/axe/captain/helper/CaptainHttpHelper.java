package org.axe.captain.helper;

import java.util.ArrayList;
import java.util.List;

import org.axe.captain.bean.TeamTable;
import org.axe.captain.constant.CaptainExceptionEnum;
import org.axe.util.HttpUtil;
import org.axe.util.JsonUtil;
import org.axe.util.StringUtil;

/**
 * Captain http 助手类
 * Created by CaiDongYu on 2016年6月15日 上午10:23:53.
 */
public final class CaptainHttpHelper {

	public static void askAndRefreshTeamTable(String url) throws Exception{
		String result = HttpUtil.sendGet(url);
		CaptainExceptionEnum exception = CaptainExceptionEnum.getException(result);
		if(exception != null){
			throw new Exception("Captain http failed ："+exception.desc);
		}
		try {
			if(StringUtil.isEmpty(result)){
				throw new Exception("http response result is empty");
			}
			//#新表
			List<?> newHosts = JsonUtil.fromJson(result,ArrayList.class);
			synchronized (TeamTable.hosts) {
				TeamTable.hosts.clear();
				for(Object obj:newHosts){
					TeamTable.hosts.add(String.valueOf(obj));
				}
			}
		} catch (Throwable e) {
			throw new Exception("Captain http failed ："+result);
		}
	}
}
