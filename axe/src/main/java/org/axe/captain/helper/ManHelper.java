package org.axe.captain.helper;

import java.util.HashMap;
import java.util.Map;

import org.axe.annotation.ioc.Component;
import org.axe.captain.interface_.Man;
import org.axe.util.HttpUtil;

/**
 * Man 助手类
 * 负责与Man沟通
 * Created by CaiDongYu on 2016年6月17日 上午9:53:41.
 */
@Component
public final class ManHelper {
	
	private Map<String,Man> mans = new HashMap<String,Man>();

	public boolean manExists(String questionTyp){
		return mans.containsKey(questionTyp);
	}
	
	public void addMan(Man man){
		mans.put(man.accpetQuestionType(), man);
	}

	public Man getMan(String questionType) {
		return mans.get(questionType);
	}
	
	public String askMan(String host, String questionType, String question){
		questionType = questionType == null?"":questionType;
		question = question == null?"":question;

		StringBuilder url = new StringBuilder(host);
		if(host.endsWith("/")){
			url.append("captain/askMan");
		}else{
			url.append("/captain/askMan");
		}
		url
		.append("?questionType=").append(questionType)
		.append("&question=").append(question);
		try {
			String result = HttpUtil.sendGet(url.toString());
			return result;
		} catch (Exception e) {
			return "";
		}
	}


}
