package org.axe.captain.helper;

import org.axe.util.HttpUtil;

/**
 * Man 助手类
 * 负责与Man 沟通
 * Created by CaiDongYu on 2016年6月15日 下午3:37:17.
 */
public final class ManHelper {

	public static String askMan(String manHost, String question) throws Exception{
		question = question == null?"":question;

		StringBuilder url = new StringBuilder(manHost);
		if(manHost.endsWith("/")){
			url.append("captain/man/askQuestion");
		}else{
			url.append("/captain/man/askQuestion");
		}
		url
		.append("?question=").append(manHost);
		try {
			String result = HttpUtil.sendGet(url.toString());
			return result;
		} catch (Exception e) {
			throw new Exception("ask Man["+url.toString()+"] failed ："+e.getMessage());
		}
	}
}
