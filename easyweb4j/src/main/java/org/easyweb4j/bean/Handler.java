package org.easyweb4j.bean;

import java.lang.reflect.Method;

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
	 * Action rest 串
	 */
	private String mappingPath;
	
    /**
     * Controller 类
     */
    private Class<?> controllerClass;

    /**
     * Action 方法
     */
    private Method actionMethod;

    public Handler(Class<?> controllerClass, Method actionMethod,String requestMethod, String mappingPath){
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
        this.requestMethod = requestMethod;
        this.mappingPath = mappingPath;
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
}
