package org.jw.bean.mvc;

import java.lang.reflect.Method;
import java.util.List;

import org.jw.annotation.mvc.Request;
import org.jw.interface_.mvc.Filter;
import org.jw.interface_.mvc.Interceptor;

/**
 * 封装 Action 信息
 * Created by CaiDongYu on 2016/4/11.
 */
public class Handler {

	/**
	 * 接受请求的类型
	 */
	private String requestMethod;
	/**
	 * 完整的 Action rest 串
	 */
	private String mappingPath;
	/**
	 * Mime-Type 类型
	 */
	private String contentType;
	/**
	 * characterEncoding 编码类型
	 */
	private String characterEncoding;
    /**
     * Controller 类
     */
    private Class<?> controllerClass;

    /**
     * Action 方法
     */
    private Method actionMethod;
    
    /**
     * Filter 链
     */
    private List<Filter> filterList;
    
    /**
     * 拦截器列表
     */
    private List<Interceptor> interceptorList;

    
    
    public Handler(String requestMethod, String mappingPath, 
			Class<?> controllerClass, Method actionMethod, List<Filter> filterList, List<Interceptor> interceptorList) {
		this.requestMethod = requestMethod;
		this.mappingPath = mappingPath;
		this.controllerClass = controllerClass;
		this.actionMethod = actionMethod;
		this.filterList = filterList;
		this.interceptorList = interceptorList;
		init();
	}

	private void init() {
    	if(this.actionMethod.isAnnotationPresent(Request.class)){
    		Request request = this.actionMethod.getAnnotation(Request.class);
    		this.contentType = request.contentType();
    		this.characterEncoding = request.characterEncoding();
    	}
	}

	public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
    
    public String getRequestMethod() {
		return requestMethod;
	}
    
    public String getMappingPath() {
		return mappingPath;
	}
    
    public List<Filter> getFilterList() {
		return filterList;
	}
    
    public List<Interceptor> getInterceptorList() {
		return interceptorList;
	}
    
    public String getContentType() {
		return contentType;
	}
    
    public String getCharacterEncoding() {
		return characterEncoding;
	}
}
