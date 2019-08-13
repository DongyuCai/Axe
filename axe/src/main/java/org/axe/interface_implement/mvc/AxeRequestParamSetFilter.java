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
package org.axe.interface_implement.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.mvc.Default;
import org.axe.annotation.mvc.RequestEntity;
import org.axe.annotation.mvc.RequestParam;
import org.axe.bean.mvc.ExceptionHolder;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.ResultHolder;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;
import org.axe.util.CastUtil;
import org.axe.util.CollectionUtil;
import org.axe.util.JsonUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.RequestUtil;
import org.axe.util.StringUtil;

/**
 * Axe 请求参数设值到Controller的filter
 * 这是Axe 提供的Filter，层级为1
 * 所以如果想在解析参数之前来执行一些操作，可以把自己定义的Filter层级设置为小于1
 * 层级大于1的自定义Filter，都在此之后执行
 */
public class AxeRequestParamSetFilter implements Filter {

	@Override
	public void init() {
		
	}

	@Override
	public int setLevel() {
		return 1;
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
		//转化请求参数到方法参数
		convertRequestParam2ActionParam(handler.getActionMethod(), param, request, response);
		return true;
	}
	
	//新方法
	private void convertRequestParam2ActionParam(Method actionMethod,Param param,HttpServletRequest request, HttpServletResponse response){
    	Type[] parameterTypes = actionMethod.getGenericParameterTypes();
    	Annotation[][] parameterAnnotations = actionMethod.getParameterAnnotations();
    	parameterTypes = parameterTypes == null?new Class<?>[0]:parameterTypes;
    	//按顺序来，塞值
    	List<Object> parameterValueList = new ArrayList<>();
    	List<String> requiredParameterError = new ArrayList<>();
    	List<String> compileParameterError = new ArrayList<>();
    	for(int i=0;i<parameterTypes.length;i++){
    		Object parameterValue = null;
    		do{
    			Type parameterType = parameterTypes[i];
    			Annotation[] parameterAnnotationAry = parameterAnnotations[i];
    			
    			RequestParam requestParam = null;
    			RequestEntity requestEntity = null;
    			Default def = null;
    			for(Annotation anno:parameterAnnotationAry){
    				if(anno instanceof RequestParam){
    					requestParam = (RequestParam)anno;
    				}else if(anno instanceof RequestEntity){
    					requestEntity = (RequestEntity)anno;
					}else if(anno instanceof Default){
						def = (Default)anno;
					}
    			}
    			
    			//## 是否@RequestParam标注的
    			if(requestParam != null){
    				String fieldName = requestParam.value();
					//TODO:除了文件数组、单文件比较特殊需要转换，其他的都按照自动类型匹配，这样不够智能
					//而且，如果fieldMap和fileMap出现同名，则会导致参数混乱，不支持同名（虽然这种情况说明代码写的真操蛋！）
					parameterValue = RequestUtil.getRequestParam(param,fieldName, parameterType);
					//默认值
					if(parameterValue == null){
						if(def != null && def.value() != null && def.value().length > 0){
							parameterValue = CastUtil.castType(def.value()[0], parameterType);
						}
					}
					
					//检测是否必填
					if(requestParam.required() && parameterValue ==  null){
    					requiredParameterError.add(StringUtil.isEmpty(requestParam.desc())?fieldName:requestParam.desc());
    				}
					
					//检查是否满足校验
					if(parameterValue != null && StringUtil.isNotEmpty(requestParam.compile())){
						Pattern compile = Pattern.compile(requestParam.compile());
						Matcher matcher = compile.matcher(String.valueOf(parameterValue));
						if(!matcher.find()){
							compileParameterError.add(StringUtil.isEmpty(requestParam.desc())?fieldName:requestParam.desc());
						}
					}
					
					break;
    			}else if(requestEntity != null){
					if(CollectionUtil.isNotEmpty(param.getBodyParamMap())){
						Map<String, Object> bodyParamMap = param.getBodyParamMap();
						//排除字段
						//userName
						//roleList.*.createTime
						for(String excludedField:requestEntity.excludedFields()){
							excludedMap(bodyParamMap, excludedField);
						}
						//默认值
						if(def != null && def.value() != null && def.value().length > 0){
							for(String defVal:def.value()){
								String key = defVal.substring(0, defVal.indexOf(":"));
								String value = "";
								if(!defVal.endsWith(":")){
									value = defVal.substring(defVal.indexOf(":")+1);
								}
								defMap(bodyParamMap, key, value);
							}
						}
						//必填字段
						if(requestEntity.requiredFields() != null){
							for(String requiredField:requestEntity.requiredFields()){
								boolean hasValue = requiredMap(bodyParamMap, requiredField);
								if(!hasValue){
									requiredParameterError.add(requiredField);
								}
							}
						}
						
						if(CollectionUtil.isEmpty(requiredParameterError)){
							Class<?> entityClass = (Class<?>)parameterType;
							String bodyParamMapJson = JsonUtil.toJson(bodyParamMap);
							try {
								parameterValue = JsonUtil.fromJson(bodyParamMapJson, entityClass);
							} catch (Exception e) {
								throw new RestException("参数格式错误，无法转换："+e.getMessage());
							}
						}
					}else{
						for(String requiredField:requestEntity.requiredFields()){
							requiredParameterError.add(requiredField);
						}
					}
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
    	
    	if(CollectionUtil.isNotEmpty(requiredParameterError)){
			throw new RestException("必填参数"+requiredParameterError.toString()+"未获取到值");
		}
    	if(CollectionUtil.isNotEmpty(compileParameterError)){
    		throw new RestException("参数"+compileParameterError.toString()+"格式错误");
    	}
    	param.setActionParamList(parameterValueList);
    }
	
	@SuppressWarnings("unchecked")
	private void excludedMap(Map<String,Object> bodyParamMap,String fieldStack){
		//把fieldStack从bodyParamMap里删掉
		int pointIndex = fieldStack.indexOf(".");
		if(pointIndex>0){
			String field = fieldStack.substring(0,pointIndex);
			String nextFieldStack = fieldStack.substring(pointIndex+1);
			Object nextBodyParamMap = bodyParamMap.get(field);
			if(nextBodyParamMap != null){
				if(Map.class.isAssignableFrom(nextBodyParamMap.getClass())){
					excludedMap((Map<String,Object>)nextBodyParamMap, nextFieldStack);
				}else if(List.class.isAssignableFrom(nextBodyParamMap.getClass())){
					excludedList((List<Object>)nextBodyParamMap, nextFieldStack);
				}
			}
		}else{
			bodyParamMap.remove(fieldStack);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void excludedList(List<Object> list,String fieldStack){
		for(Object el:list){
			if(Map.class.isAssignableFrom(el.getClass())){
				excludedMap((Map<String,Object>)el, fieldStack);
			}else if(List.class.isAssignableFrom(el.getClass())){
				excludedList((List<Object>)el, fieldStack);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void defMap(Map<String,Object> bodyParamMap,String fieldStack,Object defValue){
		//把fieldStack从bodyParamMap里删掉
		int pointIndex = fieldStack.indexOf(".");
		if(pointIndex>0){
			String field = fieldStack.substring(0,pointIndex);
			String nextFieldStack = fieldStack.substring(pointIndex+1);
			Object nextBodyParamMap = bodyParamMap.get(field);
			if(nextBodyParamMap != null){
				if(Map.class.isAssignableFrom(nextBodyParamMap.getClass())){
					defMap((Map<String,Object>)nextBodyParamMap, nextFieldStack, defValue);
				}else if(List.class.isAssignableFrom(nextBodyParamMap.getClass())){
					defList((List<Object>)nextBodyParamMap, nextFieldStack, defValue);
				}
			}
		}else{
			Object fieldValue = bodyParamMap.get(fieldStack);
			if(fieldValue == null){
				bodyParamMap.put(fieldStack, defValue);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void defList(List<Object> list,String fieldStack,Object defValue){
		for(Object el:list){
			if(Map.class.isAssignableFrom(el.getClass())){
				defMap((Map<String,Object>)el, fieldStack, defValue);
			}else if(List.class.isAssignableFrom(el.getClass())){
				defList((List<Object>)el, fieldStack, defValue);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean requiredMap(Map<String,Object> bodyParamMap,String fieldStack){
		//把fieldStack从bodyParamMap里删掉
		int pointIndex = fieldStack.indexOf(".");
		if(pointIndex>0){
			String field = fieldStack.substring(0,pointIndex);
			String nextFieldStack = fieldStack.substring(pointIndex+1);
			Object nextBodyParamMap = bodyParamMap.get(field);
			if(nextBodyParamMap != null){
				if(Map.class.isAssignableFrom(nextBodyParamMap.getClass())){
					return requiredMap((Map<String,Object>)nextBodyParamMap, nextFieldStack);
				}else if(List.class.isAssignableFrom(nextBodyParamMap.getClass())){
					return requiredList((List<Object>)nextBodyParamMap, nextFieldStack);
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			Object fieldValue = bodyParamMap.get(fieldStack);
			return fieldValue!=null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private boolean requiredList(List<Object> list,String fieldStack){
		for(Object el:list){
			if(Map.class.isAssignableFrom(el.getClass())){
				return requiredMap((Map<String,Object>)el, fieldStack);
			}else if(List.class.isAssignableFrom(el.getClass())){
				return requiredList((List<Object>)el, fieldStack);
			}else{
				return false;
			}
		}
		return false;
	}
	
	/*
	//老方法
	private void convertRequestParam2ActionParam(Method actionMethod,Param param,HttpServletRequest request, HttpServletResponse response){
    	Type[] parameterTypes = actionMethod.getGenericParameterTypes();
    	Annotation[][] parameterAnnotations = actionMethod.getParameterAnnotations();
    	parameterTypes = parameterTypes == null?new Class<?>[0]:parameterTypes;
    	//按顺序来，塞值
    	List<Object> parameterValueList = new ArrayList<>();
    	List<String> requiredParameterError = new ArrayList<>();
    	for(int i=0;i<parameterTypes.length;i++){
    		Object parameterValue = null;
    		do{
    			Type parameterType = parameterTypes[i];
    			Annotation[] parameterAnnotationAry = parameterAnnotations[i];
    			
    			RequestParam requestParam = null;
    			RequestEntity requestEntity = null;
    			Default def = null;
    			for(Annotation anno:parameterAnnotationAry){
    				if(anno instanceof RequestParam){
    					requestParam = (RequestParam)anno;
    				}else if(anno instanceof RequestEntity){
    					requestEntity = (RequestEntity)anno;
					}else if(anno instanceof Default){
						def = (Default)anno;
					}
    			}
    			
    			//## 是否@RequestParam标注的
    			if(requestParam != null){
    				String fieldName = requestParam.value();
					//TODO:除了文件数组、单文件比较特殊需要转换，其他的都按照自动类型匹配，这样不够智能
					//而且，如果fieldMap和fileMap出现同名，则会导致参数混乱，不支持同名（虽然这种情况说明代码写的真操蛋！）
					parameterValue = RequestUtil.getRequestParam(param,fieldName, parameterType);
					//默认值
					if(parameterValue == null){
						if(def != null && def.value() != null && def.value().length > 0){
							parameterValue = CastUtil.castType(def.value()[0], parameterType);
						}
					}
					
					//检测是否必填
					if(requestParam.required() && parameterValue ==  null){
    					requiredParameterError.add(fieldName);
    				}
					break;
    			}else if(requestEntity != null){
					//排除字段
					Set<String> excludedFieldSet = new HashSet<>();
					if(requestEntity.excludedFields() != null){
						for(String excludedField:requestEntity.excludedFields()){
							excludedFieldSet.add(excludedField);
						}
					}
    				
    				Class<?> entityClass = (Class<?>)parameterType;
    				parameterValue = ReflectionUtil.newInstance(entityClass);//保证不是null
    				//排除的字段，是不考虑接受参数的
    				List<EntityFieldMethod> setMethodList = ReflectionUtil.getSetMethodList(entityClass,excludedFieldSet);
					if(CollectionUtil.isNotEmpty(setMethodList)){
						//必填字段
						Set<String> requiredFieldSet = new HashSet<>();
						if(requestEntity.requiredFields() != null){
							for(String requiredField:requestEntity.requiredFields()){
								requiredFieldSet.add(requiredField);
							}
						}
						
						Map<String,String> defMap = new HashMap<>();
						if(def != null && def.value() != null && def.value().length > 0){
							for(String defVal:def.value()){
								String key = defVal.substring(0, defVal.indexOf(":"));
								String value = defVal.substring(defVal.indexOf(":")+1);
								defMap.put(key, value);
							}
						}
						
						for(EntityFieldMethod efm:setMethodList){
							String fieldName = efm.getField().getName();
							Class<?> fieldType = efm.getField().getType();
							Method method = efm.getMethod();
							Object fieldValue = RequestUtil.getRequestParam(param,fieldName, fieldType);
							//默认值
							if(fieldValue == null){
								if(defMap.containsKey(fieldName)){
									fieldValue = CastUtil.castType(defMap.get(fieldName), fieldType);
								}
							}
							if(fieldValue != null){
								//如果请求参数中，有这个字段的值，就可以塞
								ReflectionUtil.invokeMethod(parameterValue, method, fieldValue);
							}
							//检测是否必填
							if(fieldValue == null && requiredFieldSet.contains(fieldName)){
								requiredParameterError.add(fieldName);
							}
						}
					}
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
    	
    	if(CollectionUtil.isNotEmpty(requiredParameterError)){
			throw new RestException("必填参数"+requiredParameterError.toString()+"未获取到值");
		}
    	param.setActionParamList(parameterValueList);
    }*/

	@Override
	public void doEnd(HttpServletRequest request, HttpServletResponse response, Param param, Handler handler,
			ResultHolder resultHolder, ExceptionHolder exceptionHolder) {}

	
}
