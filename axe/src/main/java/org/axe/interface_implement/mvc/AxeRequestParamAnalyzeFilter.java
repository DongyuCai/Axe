package org.axe.interface_implement.mvc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.mvc.RequestParam;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;
import org.axe.helper.mvc.AjaxRequestHelper;
import org.axe.helper.mvc.FormRequestHelper;
import org.axe.interface_.mvc.Filter;
import org.axe.util.ReflectionUtil;
import org.axe.util.RequestUtil;

/**
 * Axe 请求参数解析Filter
 * 这是Axe 提供的Filter，层级为0
 * 所以如果想在解析参数之前来执行一些操作，可以把自己定义的Filter层级设置为小于0
 * 层级大于0的自定义Filter，都在此之后执行
 */
public class AxeRequestParamAnalyzeFilter implements Filter {

	@Override
	public void init() {
		
	}

	@Override
	public int setLevel() {
		return 0;
	}

	@Override
	public Pattern setMapping() {
		return Pattern.compile("^.*$");
	}

	@Override
	public Pattern setNotMapping() {
		return null;
	}

	@Override
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler)
			throws RestException {
		//解析resquest中参数
		analyzeRequestParam(request, param, handler);
		//转化请求参数到方法参数
		convertRequestParam2ActionParam(handler.getActionMethod(), param, request, response);
		return true;
	}
	
	public static void analyzeRequestParam(HttpServletRequest request, Param param, Handler handler){
		if(FormRequestHelper.isMultipart(request)){
            //如果是文件上传
            FormRequestHelper.initParam(param,request,param.getRequestPath(),handler.getMappingPath());
        }else{
            //如果不是
            try {
				AjaxRequestHelper.initParam(param,request,param.getRequestPath(),handler.getMappingPath());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RestException(RestException.SC_INTERNAL_SERVER_ERROR,e.getMessage());
			}
        }
	}
	

	public static void convertRequestParam2ActionParam(Method actionMethod,Param param,HttpServletRequest request, HttpServletResponse response){
    	Type[] parameterTypes = actionMethod.getGenericParameterTypes();
    	Annotation[][] parameterAnnotations = actionMethod.getParameterAnnotations();
    	parameterTypes = parameterTypes == null?new Class<?>[0]:parameterTypes;
    	//按顺序来，塞值
    	List<Object> parameterValueList = new ArrayList<>();
    	for(int i=0;i<parameterTypes.length;i++){
    		Object parameterValue = null;
    		do{
    			Type parameterType = parameterTypes[i];
    			Annotation[] parameterAnnotationAry = parameterAnnotations[i];
    			
    			RequestParam requestParam = null;
    			for(Annotation anno:parameterAnnotationAry){
    				if(anno instanceof RequestParam){
    					requestParam = (RequestParam)anno;
    					break;
    				}
    			}
    			
    			//## 是否@RequestParam标注的
    			if(requestParam != null){
    				String fieldName = requestParam.value();
					//TODO:除了文件数组、单文件比较特殊需要转换，其他的都按照自动类型匹配，这样不够智能
					//而且，如果fieldMap和fileMap出现同名，则会导致参数混乱，不支持同名（虽然这种情况说明代码写的真操蛋！）
					parameterValue = RequestUtil.getRequestParam(param,fieldName, parameterType);
    				break;
    			}else{
    				Class<?> parameterClass = null; 
    				if(parameterType instanceof Class){
    					parameterClass = (Class<?>)parameterType;
    				}else if(parameterType instanceof ParameterizedType){
    					parameterClass = (Class<?>)((ParameterizedType) parameterType).getRawType();
    				}
    				if(parameterClass != null){
    					//## 不含注解的
    					//* 如果是HttpServletRequest
    					if(ReflectionUtil.compareType(HttpServletRequest.class, parameterClass)){
    						parameterValue = request;
    						break;
    					}
    					if(ReflectionUtil.compareType(HttpServletResponse.class, parameterClass)){
    						parameterValue = response;
    						break;
    					}
    					//* 如果是Param
    					if(ReflectionUtil.compareType(Param.class,parameterClass)){
    						parameterValue = param;
    						break;
    					}
    					//* 如果是Map<String,Object> 
    					if(ReflectionUtil.compareType(Map.class, parameterClass)){
    						parameterValue = param.getBodyParamMap();
    						break;
    					}
    					
    				}
    			}
    			
    			//## 其他杂七杂八类型，只能给null，框架不管
    		}while(false);
    		parameterValueList.add(parameterValue);
    	}
    	
    	
    	param.setActionParamList(parameterValueList);
    }

	@Override
	public void doEnd(HttpServletRequest request,HttpServletResponse response,Param param,Handler handler) {}
	
}
