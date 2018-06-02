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
package org.axe.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Default;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestEntity;
import org.axe.annotation.mvc.RequestParam;
import org.axe.annotation.persistence.ColumnDefine;
import org.axe.annotation.persistence.Comment;
import org.axe.bean.mvc.FileParam;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Handler.ActionParam;
import org.axe.bean.mvc.Header;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.helper.mvc.ControllerHelper;
import org.axe.interface_.mvc.Filter;
import org.axe.interface_.mvc.Interceptor;
import org.axe.interface_implement.mvc.HeaderFilter;

/**
 * Rest接口导出工具类
 * @author CaiDongyu on 2018年2月3日 上午10:00:55.
 */
public class ApiExportUtil {

	private ApiExportUtil() {}
	
	public final static class Level_1{
		private int index;
		private String title;
		private String controllerClassName;
		private List<Level_2> requestList;
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public List<Level_2> getRequestList() {
			return requestList;
		}
		public void setRequestList(List<Level_2> requestList) {
			this.requestList = requestList;
		}
		public String getControllerClassName() {
			return controllerClassName;
		}
		public void setControllerClassName(String controllerClassName) {
			this.controllerClassName = controllerClassName;
		}
		
	}
	
	public final static class Level_2{
		private int index;
		private String controllerTitle;
		private String controllerClassName;
		private String requestTitle;
		private String requestMethodName;
		private String url;
		private String method;
		private List<String> filterList;
		private List<String> interceptorList;
		private List<Header> headerList;
		private Map<String,Object> requestParamBody;
		private Map<String,Object> requestParamBodyFormat;
		
		public String getControllerClassName() {
			return controllerClassName;
		}
		public void setControllerClassName(String controllerClassName) {
			this.controllerClassName = controllerClassName;
		}
		public String getRequestMethodName() {
			return requestMethodName;
		}
		public void setRequestMethodName(String requestMethodName) {
			this.requestMethodName = requestMethodName;
		}
		public String getControllerTitle() {
			return controllerTitle;
		}
		public void setControllerTitle(String controllerTitle) {
			this.controllerTitle = controllerTitle;
		}
		public String getRequestTitle() {
			return requestTitle;
		}
		public void setRequestTitle(String requestTitle) {
			this.requestTitle = requestTitle;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public List<String> getFilterList() {
			return filterList;
		}
		public void setFilterList(List<String> filterList) {
			this.filterList = filterList;
		}
		public List<String> getInterceptorList() {
			return interceptorList;
		}
		public void setInterceptorList(List<String> interceptorList) {
			this.interceptorList = interceptorList;
		}
		public List<Header> getHeaderList() {
			return headerList;
		}
		public void setHeaderList(List<Header> headerList) {
			this.headerList = headerList;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Map<String, Object> getRequestParamBody() {
			return requestParamBody;
		}
		public void setRequestParamBody(Map<String, Object> requestParamBody) {
			this.requestParamBody = requestParamBody;
		}
		public Map<String, Object> getRequestParamBodyFormat() {
			return requestParamBodyFormat;
		}
		public void setRequestParamBodyFormat(Map<String, Object> requestParamBodyFormat) {
			this.requestParamBodyFormat = requestParamBodyFormat;
		}
		
	}
	
	public static List<Level_1> asApiTest(String basePath){
		StringBuilder requestParamFormatBuf = new StringBuilder();
		
		
		List<Level_1> list = new ArrayList<>();
		Map<String,Integer> map = new HashMap<>();//存下标
		//获取Controller 到 action的关系表
		List<Handler> handlerList = ControllerHelper.getActionList();
		for(Handler handler:handlerList){
			Class<?> controllerClass = handler.getControllerClass();
			Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
			//没有title不要
			String controllerTitle = controllerAnnotation.title();
			if(StringUtil.isEmpty(controllerTitle)) continue;
			
			//存第一级
			Integer listIndex = map.get(controllerTitle);
			Level_1 level_1 = null;
			if(listIndex == null){
				//新增一个
				level_1 = new Level_1();
				list.add(level_1);
				listIndex = list.size()-1;
				level_1.setIndex(listIndex);
				level_1.setTitle(controllerTitle);
				level_1.setControllerClassName(controllerClass.getName());
				map.put(controllerTitle, listIndex);
			}else{
				level_1 = list.get(listIndex);
			}

			//存第二级
			List<Level_2> requestList = level_1.getRequestList();
			if(requestList == null){
				requestList = new ArrayList<>();
				level_1.setRequestList(requestList);
			}
			Method actionMethod = handler.getActionMethod();
			Request requestAnnotation = actionMethod.getAnnotation(Request.class);
			
			String requestTitle = requestAnnotation.title();
			if(StringUtil.isEmpty(requestTitle)) continue;//如果action没有标题，也不要
			
			Level_2 level_2 = new Level_2();
			requestList.add(level_2);
			//类名称
			level_2.setIndex(requestList.size()-1);
			level_2.setControllerTitle(controllerTitle);
			level_2.setControllerClassName(level_1.getControllerClassName());
			//方法名称
			level_2.setRequestTitle(requestTitle);
			level_2.setRequestMethodName(actionMethod.getName());
			//url
			level_2.setUrl(basePath+handler.getMappingPath());
			//POST DELETE PUT GET
			level_2.setMethod(handler.getRequestMethod());
			//filter 名字列表
			List<Filter> filterList = handler.getFilterList();
			if(CollectionUtil.isNotEmpty(filterList)){
				List<String> filterNameList = new ArrayList<>();
				String blank = "&nbsp;&nbsp;";
				for(Filter filter:filterList){
					filterNameList.add(blank+filter.setLevel()+"&nbsp;&nbsp;"+filter.getClass().getName());
					blank = blank+"&nbsp;&nbsp;";
				}
				level_2.setFilterList(filterNameList);
			}
			//interceptor 名字列表
			List<Interceptor> interceptorList = handler.getInterceptorList();
			if(CollectionUtil.isNotEmpty(interceptorList)){
				List<String> interceptorNameList = new ArrayList<>();
				String blank = "&nbsp;&nbsp;";
				for(Interceptor interceptor:interceptorList){
					interceptorNameList.add(blank+"&nbsp;&nbsp;"+interceptor.getClass().getName());
					blank = blank+"&nbsp;&nbsp;";
				}
				level_2.setInterceptorList(interceptorNameList);
			}
			//header
			List<Header> headerList = new ArrayList<>();
			level_2.setHeaderList(headerList);
			if(CollectionUtil.isNotEmpty(filterList)){
				//找下有没有HeaderFilter
				for(Filter filter:filterList){
					if(HeaderFilter.class.isAssignableFrom(filter.getClass())){
						Header[] headers = ((HeaderFilter)filter).headers();
						if(headers != null){
							for(Header header:headers){
								if(header != null){
									headerList.add(header);
								}
							}
						}
					}
				}
			}
			
			//request param
			Map<String,Object> requestParamBody = new HashMap<>();
			Map<String,Object> requestParamBodyFormat = new HashMap<>();
			level_2.setRequestParamBody(requestParamBody);
			level_2.setRequestParamBodyFormat(requestParamBodyFormat);
			List<ActionParam> actionParamList = handler.getActionParamList();
			if(CollectionUtil.isNotEmpty(actionParamList)){
				for(ActionParam ap:actionParamList){
					Annotation[] annotations = ap.getAnnotations();
					if(annotations != null){
						RequestParam rp = null;
						RequestEntity re = null;
						Default def = null;
						for(Annotation annotation:annotations){
							if(annotation instanceof RequestParam){
								rp = (RequestParam)annotation;
							}else if(annotation instanceof RequestEntity){
								re = (RequestEntity)annotation;
							}else if(annotation instanceof Default){
								def = (Default)annotation;
							}
						}
						
						if(rp != null){
							//如果RequestParam存在，但是url里不包含此参数，那么需要加入到Param里
							//url包含的参数，是不需要放到requestParamList里的
							String name = rp.value();
							String required = "否";
							String type = ap.getParamType().getSimpleName();
							String defaultValue = null;
							String desc = null;
							if(rp.required() || handler.getMappingPath().contains("{"+rp.value()+"}")){
								//有required注解，或者url中包含，都可以算必填
								required = "是";
							}
							if(def != null && def.value() != null && def.value().length > 0){
								defaultValue = def.value()[0];
							}
							if(StringUtil.isNotEmpty(rp.desc())){
								desc = rp.desc();
							}
							
							requestParamFormatBuf.delete(0, requestParamFormatBuf.length());
							if(StringUtil.isNotEmpty(required)){
								requestParamFormatBuf.append("[").append("必填：").append(required).append("]");
							}
							if(StringUtil.isNotEmpty(type)){
								requestParamFormatBuf.append("[").append("类型：").append(type).append("]");
							}
							if(StringUtil.isNotEmpty(defaultValue)){
								requestParamFormatBuf.append("[").append("默认值：").append(defaultValue).append("]");
							}
							if(StringUtil.isNotEmpty(desc)){
								requestParamFormatBuf.append("[").append("含义：").append(desc).append("]");
							}
							requestParamBody.put(name, defaultValue);
							requestParamBodyFormat.put(name, requestParamFormatBuf.toString());
							requestParamFormatBuf.delete(0, requestParamFormatBuf.length());
						}else if(re != null){
							Set<String> excludedFieldSet = new HashSet<>();
							Set<String> requiredFieldSet = new HashSet<>();
							Map<String,String> defValueMap = new HashMap<>();
							Set<String> keyHistory = new HashSet<>();
							askEachField("", requestParamBody, requestParamBodyFormat, ap.getParamType(), excludedFieldSet, requiredFieldSet, defValueMap, keyHistory, requestParamFormatBuf);
						}
					}
				}
			}
		}
		return list;
	}
	
	// 注意，如果是集合类型数据结构，那么只能是List和Map
	private static void askEachField(
			String rootFieldName,
			Map<String, Object> bodyParamMap, 
			Map<String, Object> bodyParamMapFormat,
			Class<?> entityClass, 
			Set<String> excludedFieldSet, 
			Set<String> requiredFieldSet,
			Map<String,String> defValueMap,
			Set<String> keyHistory,
			StringBuilder buf) {
		List<EntityFieldMethod> setMethodList = ReflectionUtil.getSetMethodList(entityClass);
		
		List<Field> listField = new ArrayList<>();
		List<Field> mapField = new ArrayList<>();
		List<Field> entityField = new ArrayList<>();
		for (EntityFieldMethod ef : setMethodList) {
			Field field = ef.getField();
			Type genericType = field.getGenericType();// List<?>
			Class<?> rawType = field.getType();// List
			
			String historyFlag = entityClass.getName()+"#"+rawType.getName()+"#"+field.getName();
			if(keyHistory.contains(historyFlag)){
				continue;//防止自定义类自身嵌套死循环
			}else{
				keyHistory.add(historyFlag);
			}
			
			if (genericType instanceof ParameterizedType) {
				// 说明带泛型
				if (List.class.isAssignableFrom(rawType)) {
					listField.add(field);//保证结构完整，先解析当前层的，这种嵌套类型的后解析
				} else if (Map.class.isAssignableFrom(rawType)) {
					mapField.add(field);
				}
			} else {
				// 不带泛型，那有三种可能
				// 1.基本类型
				// 2.List
				// 3.Map
				// 4.自定义
				// 2和3就没办法考虑了，只能考虑1和4
				if (ReflectionUtil.compareType(String.class, rawType) || ReflectionUtil.compareType(Byte.class, rawType)
						|| ReflectionUtil.compareType(Boolean.class, rawType)
						|| ReflectionUtil.compareType(Short.class, rawType)
						|| ReflectionUtil.compareType(Character.class, rawType)
						|| ReflectionUtil.compareType(Integer.class, rawType)
						|| ReflectionUtil.compareType(Long.class, rawType)
						|| ReflectionUtil.compareType(Float.class, rawType)
						|| ReflectionUtil.compareType(Double.class, rawType)
						|| rawType.getName().startsWith("java.")){
					setBodyMap(rootFieldName, bodyParamMap, bodyParamMapFormat, excludedFieldSet, requiredFieldSet, defValueMap, buf, field);
				} else if ((rawType).isPrimitive()) {
					setBodyMap(rootFieldName, bodyParamMap, bodyParamMapFormat, excludedFieldSet, requiredFieldSet, defValueMap, buf, field);
				} else if (List.class.isAssignableFrom(rawType)) {
					// 什么都不做
				} else if (Map.class.isAssignableFrom(rawType)) {
					// 什么都不做
				} else {
					//自定义类型，也是放到后面处理，防止层级结构混乱
					entityField.add(field);
				}
			}
		}
		

		for (Field field : listField) {
			Type genericType = field.getGenericType();// List<?>
			Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
			
			// 如果是List，那么就新建一个列表，加一个元素，然后继续元素迭代
			List<Object> list = new ArrayList<>();
			Map<String, Object> listChildMap = new HashMap<>();
			list.add(listChildMap);
			bodyParamMap.put(field.getName(), list);
			
			List<Object> listFormat = new ArrayList<>();
			Map<String, Object> listChildMapFormat = new HashMap<>();
			listFormat.add(listChildMapFormat);
			bodyParamMapFormat.put(field.getName(), listFormat);
			
			askEachField(rootFieldName+field.getName()+".",listChildMap, listChildMapFormat, (Class<?>) actualTypes[0], excludedFieldSet, requiredFieldSet,
					defValueMap,keyHistory,buf);
		}

		for (Field field : mapField) {
			Type genericType = field.getGenericType();// List<?>
			Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
			
			// 如果是Map，那么就新建一个Map，Map的键肯地只能是String，那么value进行迭代
			Map<String, Object> map = new HashMap<>();
			bodyParamMap.put(field.getName(), map);
			
			Map<String, Object> mapFormat = new HashMap<>();
			bodyParamMapFormat.put(field.getName(), mapFormat);
			
			askEachField(rootFieldName+field.getName()+".",map, mapFormat, (Class<?>) actualTypes[1], excludedFieldSet, requiredFieldSet,
					defValueMap,keyHistory,buf);
		}
		
		for(Field field : entityField){
			Class<?> rawType = field.getType();// List
			
			// 就是自定义类型
			Map<String, Object> map = new HashMap<>();
			bodyParamMap.put(field.getName(), map);

			Map<String, Object> mapFormat = new HashMap<>();
			bodyParamMapFormat.put(field.getName(), mapFormat);
			
			askEachField(rootFieldName+field.getName()+".",map, mapFormat, rawType, excludedFieldSet, requiredFieldSet, defValueMap,keyHistory,buf);
		}
		
	}
	
	private static void setBodyMap(
			String rootFieldName,
			Map<String, Object> bodyParamMap, 
			Map<String, Object> bodyParamMapFormat,
			Set<String> excludedFieldSet, 
			Set<String> requiredFieldSet,
			Map<String, String> defValueMap, 
			StringBuilder buf,
			Field field){
		do{
			String name = field.getName();
			if(excludedFieldSet.contains(rootFieldName+name)){
				break;//排除掉
			}
			
			
			buf.delete(0, buf.length());
			String required = "否";
			String type = field.getType().getSimpleName();
			String defaultValue = "";
			String desc = null;
			if(requiredFieldSet.contains(rootFieldName+name)){
				//有required注解，或者url中包含，都可以算必填
				required = "是";
			}
			if(defValueMap.containsKey(rootFieldName+name)){
				defaultValue = defValueMap.get(rootFieldName+name);
			}
			Comment comment = field.getAnnotation(Comment.class);
			if(comment != null){
				desc = comment.value();
			}
			
			ColumnDefine columnDefine = field.getAnnotation(ColumnDefine.class);
			if(columnDefine != null && columnDefine.value().toUpperCase().indexOf(" COMMENT ")>0){
				int index = columnDefine.value().toUpperCase().indexOf(" COMMENT ");
				int indexStart = columnDefine.value().toUpperCase().indexOf("'", index);
				int indexEnd = columnDefine.value().toUpperCase().indexOf("'", indexStart+1);
				if(indexStart < indexEnd){
					desc = columnDefine.value().substring(indexStart+1, indexEnd);
				}
			}
			
			buf.delete(0, buf.length());
			if(StringUtil.isNotEmpty(required)){
				buf.append("[").append("必填：").append(required).append("]");
			}
			if(StringUtil.isNotEmpty(type)){
				buf.append("[").append("类型：").append(type).append("]");
			}
			if(StringUtil.isNotEmpty(defaultValue)){
				buf.append("[").append("默认值：").append(defaultValue).append("]");
			}
			if(StringUtil.isNotEmpty(desc)){
				buf.append("[").append("含义：").append(desc).append("]");
			}
			
			bodyParamMap.put(name, defaultValue);
			bodyParamMapFormat.put(name, buf.toString());
			buf.delete(0, buf.length());
		}while(false);
	}
	
	/**
	 * 转成Postman文件 格式为 collection format v2
	 * 例子：asPostmanV2("xxxapi接口","http://
	 */
	public static Map<String,Object> asPostmanV2(String name,String basePath){
		//获取Controller 到 action的关系表
		List<Handler> actionList = ControllerHelper.getActionList();
		Map<Class<?>,List<Handler>> actionMap = new HashMap<>();
		for(Handler handler:actionList){
			List<Handler> handlerList = actionMap.get(handler.getControllerClass());
			if(handlerList == null){
				handlerList = new ArrayList<>();
				actionMap.put(handler.getControllerClass(), handlerList);
			}
			handlerList.add(handler);
		}
		
		//开始配置json导入文件
		Map<String,Object> config = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(new Date());
		Map<String,String> info = new HashMap<>();
		info.put("name", name+"["+format+"]");
		info.put("_postman_id", StringUtil.getRandomString(8)+
								"-"+StringUtil.getRandomString(4)+
								"-"+StringUtil.getRandomString(4)+
								"-"+StringUtil.getRandomString(4)+
								"-"+StringUtil.getRandomString(12));
		info.put("schema", "https://schema.getpostman.com/json/collection/v2.0.0/collection.json");
		config.put("info", info);
		
		List<Map<String,Object>> folderList = new ArrayList<>();
		config.put("item", folderList);
		
		//第一级是文件夹
		for(Class<?> controllerClass:actionMap.keySet()){
			Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
			//类没有名字的不显示
			if(StringUtil.isEmpty(controllerAnnotation.title())) continue;
			
			Map<String,Object> folder = new HashMap<>();
			folder.put("name", controllerAnnotation.title());
			folder.put("description", controllerClass.getName());
			folderList.add(folder);
			List<Handler> handlerList = actionMap.get(controllerClass);
			List<Map<String,Object>> urlList = new ArrayList<>();
			for(Handler handler:handlerList){
				Method actionMethod = handler.getActionMethod();
				Request requestAnnotation = actionMethod.getAnnotation(Request.class);
				//action没有名字的，也过掉
				if(StringUtil.isEmpty(requestAnnotation.title())) continue;
				
				Map<String,Object> url = new HashMap<>();
				url.put("name", requestAnnotation.title());
				Map<String,Object> requestConfig = new HashMap<>();
				requestConfig.put("description", controllerClass.getName()+"#"+actionMethod.getName());
				requestConfig.put("url", basePath+handler.getMappingPath());
				requestConfig.put("method", handler.getRequestMethod());
				Map<String,Object> body = new HashMap<>();
				requestConfig.put("body",body);
				List<Filter> filterList = handler.getFilterList();
				if(CollectionUtil.isNotEmpty(filterList)){
					//拼装header
					List<Map<String,String>> headerList = new ArrayList<>();
					//找下有没有HeaderFilter
					for(Filter filter:filterList){
						if(HeaderFilter.class.isAssignableFrom(filter.getClass())){
							Header[] headers = ((HeaderFilter)filter).headers();
							if(headers != null){
								for(Header header:headers){
									if(header != null){
										Map<String,String> headerMap = new HashMap<>();
										headerMap.put("key", header.getName());
										headerMap.put("value", header.getValue());
										headerList.add(headerMap);
									}
								}
							}
						}
					}
					requestConfig.put("header",headerList);
				}
				
				//body分POST、PUT、DELETE  和    GET 三种
				List<ActionParam> actionParamList = handler.getActionParamList();
				if("POST,PUT,DELETE".contains(handler.getRequestMethod())){
					body.put("mode", "formdata");
					List<Map<String,String>> formdataList = new ArrayList<>();
					if(CollectionUtil.isNotEmpty(actionParamList)){
						for(ActionParam ap:actionParamList){
							Annotation[] annotations = ap.getAnnotations();
							if(annotations != null){
								RequestParam rp = null;
								RequestEntity re = null;
								for(Annotation annotation:annotations){
									if(annotation instanceof RequestParam){
										rp = (RequestParam)annotation;
									}else if(annotation instanceof RequestEntity){
										re = (RequestEntity)annotation;
									}
								}
								
								if(rp != null){
									Map<String,String> formdata = new HashMap<>();
									formdata.put("key", rp.value());
									if(StringUtil.isNotEmpty(rp.desc())){
										formdata.put("description", rp.desc());
									}
									if(ReflectionUtil.compareType(ap.getParamType(), FileParam.class)){
										formdata.put("type", "file");
									}else{
										formdata.put("type", "text");
									}
									formdataList.add(formdata);
								}else if(re != null){
									List<EntityFieldMethod> setMethodList = ReflectionUtil.getSetMethodList(ap.getParamType());
									if(CollectionUtil.isNotEmpty(setMethodList)){
										for(EntityFieldMethod fm:setMethodList){
											Map<String,String> formdata = new HashMap<>();
											String fieldName = fm.getField().getName();
											formdata.put("key", fieldName);
											formdata.put("type", "text");
											Comment comment = fm.getField().getAnnotation(Comment.class);
											if(comment != null){
												formdata.put("description", comment.value());
											}
											formdataList.add(formdata);
										}
									}
								}
							}
						}
					}
					body.put("formdata", formdataList);
				}else{
					//GET
					body.put("mode", "raw");
					body.put("raw", "");
					//拼写参数到url末尾
					String urlParam = "";
					if(CollectionUtil.isNotEmpty(actionParamList)){
						for(ActionParam ap:actionParamList){
							Annotation[] annotations = ap.getAnnotations();
							if(annotations != null){
								for(Annotation rqat:annotations){
									if(rqat instanceof RequestParam){
										if(StringUtil.isNotEmpty(urlParam)){
											urlParam = urlParam+"&";
										}
										urlParam = urlParam+((RequestParam)rqat).value()+"=";
										break;
									}
								}
							}
						}
					}
					if(StringUtil.isNotEmpty(urlParam)){
						requestConfig.put("url",requestConfig.get("url")+"?"+urlParam);
					}
				}
				
				url.put("request", requestConfig);
				urlList.add(url);
			}
			folder.put("item", urlList);
		}
		//第二级是url
		
		return config;
	}
	
}
