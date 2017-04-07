package org.axe.constant;

/**
 * Http Content Type类型
 * Created by CaiDongYu on 2016年5月10日 上午8:29:06.
 */
public enum ContentType {
	
	APPLICATION_JSON("application/json"),
	APPLICATION_HTML("text/html; charset=utf-8");
	
	public String CONTENT_TYPE;

	private ContentType(String cONTENT_TYPE) {
		CONTENT_TYPE = cONTENT_TYPE;
	}
	
}
