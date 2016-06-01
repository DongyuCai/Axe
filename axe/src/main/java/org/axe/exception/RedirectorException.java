package org.axe.exception;

/**
 * 提供异常跳转服务
 * Created by CaiDongYu on 2016年6月1日 下午1:59:03.
 */
public class RedirectorException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String path;
	
	public RedirectorException(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
}
