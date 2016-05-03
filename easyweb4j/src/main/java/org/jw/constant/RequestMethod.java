package org.jw.constant;


/**
 * 请求类型注解
 * Created by CaiDongYu on 2016/4/8.
 */
public enum RequestMethod {
	POST("POST"),
	DELETE("DELETE"),
	PUT("PUT"),
	GET("GET");
	
	public String REQUEST_METHOD;
	
	private RequestMethod(String method) {
		this.REQUEST_METHOD = method;
	}
}
