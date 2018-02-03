package org.axe.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestEntity;
import org.axe.annotation.mvc.RequestParam;
import org.axe.annotation.mvc.Required;
import org.axe.annotation.persistence.Comment;
import org.axe.bean.mvc.FileParam;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Handler.ActionParam;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.helper.mvc.ControllerHelper;

/**
 * Rest接口导出工具类
 * Created by CaiDongYu on 2018年2月3日 上午10:00:55.
 */
public class ApiExportUtil {
	
	/**
	 * 转成Postman文件 格式为 collection format v2
	 * 例子：asPostmanV2("xxxapi接口","http://
	 */
	public static Map<String,Object> asPostmanV2(String name,String basePath,Map<String,String> header){
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
				if(CollectionUtil.isNotEmpty(header)){
					//拼装header
					List<Map<String,String>> headerList = new ArrayList<>();
					for(String headerKey:header.keySet()){
						Map<String,String> headerMap = new HashMap<>();
						headerMap.put("key", headerKey);
						headerMap.put("value", headerMap.get(headerKey));
						headerList.add(headerMap);
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
								Required required = null;
								for(Annotation annotation:annotations){
									if(annotation instanceof RequestParam){
										rp = (RequestParam)annotation;
									}else if(annotation instanceof RequestEntity){
										re = (RequestEntity)annotation;
									}else if(annotation instanceof Required){
										required = (Required)annotation;
									}
								}
								
								if(rp != null){
									Map<String,String> formdata = new HashMap<>();
									formdata.put("key", rp.value());
									if(StringUtil.isNotEmpty(rp.desc())){
										formdata.put("description", rp.desc());
									}
									if(required != null){
										formdata.put("required", "true");
									}
									if(ReflectionUtil.compareType(ap.getParamType(), FileParam.class)){
										formdata.put("type", "file");
									}else{
										formdata.put("type", "text");
									}
									formdataList.add(formdata);
								}else if(re != null){
									Set<String> requiredFieldSet = new HashSet<>();
									if(required != null){
										for(String requiredField:required.value()){
											requiredFieldSet.add(requiredField);
										}
									}
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
											if(requiredFieldSet.contains(fieldName)){
												formdata.put("required", "true");
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
