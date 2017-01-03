package org.axe.constant;

/**
 * Http 字符 编码
 * Created by CaiDongYu on 2016年5月10日 上午8:27:31.
 */
public enum CharacterEncoding {
	
	UTF_8("UTF-8"); 
	
	public String CHARACTER_ENCODING;

	private CharacterEncoding(String cHARACTER_ENCODING) {
		CHARACTER_ENCODING = cHARACTER_ENCODING;
	}
}
