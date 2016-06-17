package org.axe.captain.interface_;
/**
 * 组员行为的抽象
 * Created by CaiDongYu on 2016年6月17日 上午9:47:18.
 */
public interface Man {


	/**
	 * 接受什么类型问题
	 */
	public String accpetQuestionType();
	
	/**
	 * 回答问题
	 */
	public Object answerQuestion(String question);
}
