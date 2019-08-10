/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.bean.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.axe.annotation.mvc.Request;
import org.axe.interface_.mvc.Filter;
import org.axe.interface_.mvc.Interceptor;

/**
 * 封装 Action 信息
 * @author CaiDongyu on 2016/4/11.
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
	 * 完整的mappingPath描述，也就是uri描述，来自controllerDesc+"."+actionDesc
	 */
	private String mappingPathDesc;
	
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
     * controller的描述
     */
    private String controllerDesc;

    /**
     * Action 方法
     */
    private Method actionMethod;


    /**
     * action的描述
     */
    private String actionDesc;
    
    public class ActionParam{
    	private Class<?> paramType;
    	private Annotation[] annotations;
		public Class<?> getParamType() {
			return paramType;
		}
		public void setParamType(Class<?> paramType) {
			this.paramType = paramType;
		}
		public Annotation[] getAnnotations() {
			return annotations;
		}
		public void setAnnotations(Annotation[] annotations) {
			this.annotations = annotations;
		}
    }
    /**
     * Action 参数
     */
    private List<ActionParam> actionParamList;
    
    /**
     * Filter 链
     */
    private List<Filter> filterList;
    
    /**
     * 拦截器列表
     */
    private List<Interceptor> interceptorList;

    
    
    public Handler(String requestMethod, String mappingPath, 
			Class<?> controllerClass, String controllerDesc, Method actionMethod, String actionDesc, List<Filter> filterList, List<Interceptor> interceptorList) {
		this.requestMethod = requestMethod;
		this.mappingPath = mappingPath;
		this.mappingPathDesc = controllerDesc+"."+actionDesc;
		this.controllerClass = controllerClass;
		this.controllerDesc = controllerDesc;
		this.actionMethod = actionMethod;
		this.actionDesc = actionDesc;
		this.filterList = filterList;
		this.interceptorList = interceptorList;
		
		Request request = this.actionMethod.getAnnotation(Request.class);
		this.contentType = request.contentType().CONTENT_TYPE;
		this.characterEncoding = request.characterEncoding().CHARACTER_ENCODING;
		
		Annotation[][] parameterAnnotations = this.actionMethod.getParameterAnnotations();
		Class<?>[] parameterTypes = this.actionMethod.getParameterTypes();
		if(parameterTypes != null){
			if(parameterTypes.length == parameterAnnotations.length){
				for(int i=0;i<parameterTypes.length;i++){
					Class<?> paramType = parameterTypes[i];
					Annotation[] annotations = parameterAnnotations[i];
					if(actionParamList == null){
						actionParamList = new ArrayList<>();
					}
					ActionParam actionParam = new ActionParam();
					actionParam.setAnnotations(annotations);
					actionParam.setParamType(paramType);
					actionParamList.add(actionParam);
				}
			}else{
				throw new RuntimeException("create Hanlder failed ,wrong parameterTypes.length["+parameterTypes.length+"] and "
						+ "parameterAnnotations.length["+parameterAnnotations.length+"]: "+this.actionMethod.toGenericString());
			}
		}
	}

	public Class<?> getControllerClass() {
        return controllerClass;
    }

    public String getControllerDesc() {
		return controllerDesc;
	}

	public Method getActionMethod() {
        return actionMethod;
    }
    
    public String getActionDesc() {
		return actionDesc;
	}

	public String getRequestMethod() {
		return requestMethod;
	}
    
    public String getMappingPath() {
		return mappingPath;
	}
    
    public String getMappingPathDesc() {
		return mappingPathDesc;
	}

	public List<ActionParam> getActionParamList() {
		return actionParamList;
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
