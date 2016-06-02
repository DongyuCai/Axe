package org.axe.exception;

/**
 * 提供异中断转服务
 * 特别在Filter和Interceptor中，因为只能返回true或false，所以借助此类，可以请求中断跳转
 * Created by CaiDongYu on 2016年6月1日 下午1:59:03.
 */
public class RedirectorInterrupt extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String path;
	
	public RedirectorInterrupt(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
}
