package org.axe.captain.service;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Service;
import org.axe.captain.interface_.Man;

/**
 * 组员
 * Created by CaiDongYu on 2016年6月15日 下午3:30:31.
 */
@Service
public class ManService {

	@Autowired
	private Man man;
	

	public Object answerQuestion(String question) {
		if(man != null){
			return man.answerQuestion(question);
		}
		return null;
	}
	
}
