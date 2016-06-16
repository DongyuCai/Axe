package org.axe.captain.helper;

import java.util.HashMap;
import java.util.Map;

import org.axe.annotation.ioc.Component;
import org.axe.captain.bean.TeamTable;
import org.axe.captain.interface_.Captain;
import org.axe.util.HttpUtil;

/**
 * Captain 助手类
 * 负责与Captain沟通
 * Created by CaiDongYu on 2016年6月15日 下午3:37:17.
 */
@Component
public final class CaptainHelper {
	
	private Map<String,Captain> captains = new HashMap<String,Captain>();

	public boolean captainExists(String questionTyp){
		return captains.containsKey(questionTyp);
	}
	
	public void addCaptain(Captain captain){
		captains.put(captain.accpetQuestionType(), captain);
	}

	public Captain getCaptain(String questionType) {
		return captains.get(questionType);
	}
	
	public String askCaptain(String questionType, String question) throws Exception{
		questionType = questionType == null?"":questionType;
		question = question == null?"":question;

		String captain = TeamTable.getCaptain();
		StringBuilder url = new StringBuilder(captain);
		if(captain.endsWith("/")){
			url.append("captain/askQuestion");
		}else{
			url.append("/captain/askQuestion");
		}
		url
		.append("?questionType=").append(questionType)
		.append("&question=").append(question);
		try {
			String result = HttpUtil.sendGet(url.toString());
			return result;
		} catch (Exception e) {
			throw new Exception("ask Captain["+url.toString()+"] failed ："+e.getMessage());
		}
	}


}
