package org.axe.captain.helper;

import org.axe.captain.bean.TeamTable;
import org.axe.util.HttpUtil;

/**
 * Captain 助手类
 * 负责与Captain沟通
 * Created by CaiDongYu on 2016年6月15日 下午3:37:17.
 */
public final class CaptainHelper {

	
	public static String askCaptain(String question) throws Exception{
		question = question == null?"":question;

		String captain = TeamTable.getCaptain();
		StringBuilder url = new StringBuilder(captain);
		if(captain.endsWith("/")){
			url.append("captain/askQuestion");
		}else{
			url.append("/captain/askQuestion");
		}
		url
		.append("?question=").append(question);
		try {
			String result = HttpUtil.sendGet(url.toString());
			return result;
		} catch (Exception e) {
			throw new Exception("ask Captain["+url.toString()+"] failed ："+e.getMessage());
		}
	}
}
