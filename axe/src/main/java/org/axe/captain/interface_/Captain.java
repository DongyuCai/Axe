package org.axe.captain.interface_;

/**
 * Captain 角色的行为抽象
 * @author Administrator
 */
public interface Captain {

	/**
	 * 是否接受这个问题
	 */
	public String accpetQuestionType();
	
	/**
	 * 回答问题
	 */
	public Object answerQuestion(String question);
}
