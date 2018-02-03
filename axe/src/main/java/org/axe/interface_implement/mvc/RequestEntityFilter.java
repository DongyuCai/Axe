package org.axe.interface_implement.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.mvc.RequestEntity;
import org.axe.annotation.mvc.Required;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Handler.ActionParam;
import org.axe.bean.mvc.Param;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;
import org.axe.util.CastUtil;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;

public abstract class RequestEntityFilter implements Filter {

	@Override
	public void init() {
		
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
		List<ActionParam> actionParamList = handler.getActionParamList();
		List<Object> actionParamValueList = param.getActionParamList();
		List<String> error = new ArrayList<>();
		if(CollectionUtil.isNotEmpty(actionParamList)){
			if(actionParamList.size() != actionParamValueList.size()){
				throw new RestException("参数数目异常，actionParamList.size="+actionParamList.size()+"  actionParamValueList.size="+actionParamValueList.size());
			}
			
			for(int i=0;i<actionParamList.size();i++){
				ActionParam ap = actionParamList.get(i);
				Object paramValue = actionParamValueList.get(i);
				Annotation[] ats = ap.getAnnotations();
				if(ats != null){
					boolean isRequestEntity = false;
					Set<String> required = null;
					for(Annotation at:ats){
						if(at instanceof RequestEntity){
							isRequestEntity = true;
							break;
						}
						if(at instanceof  Required){
							required = new HashSet<>();
							String[] requiredAry = ((Required)at).value();
							for(String requiredField:requiredAry){
								required.add(requiredField);
							}
						}
					}
					//是个实体，需要参数映射到类中
					if(isRequestEntity){
						try {
							if(paramValue == null){
								paramValue = ReflectionUtil.newInstance(ap.getParamType());
							}
							List<EntityFieldMethod> setMethodList = ReflectionUtil.getSetMethodList(ap.getParamType());
							if(CollectionUtil.isNotEmpty(setMethodList)){
								for(EntityFieldMethod efm:setMethodList){
									String fieldName = efm.getField().getName();
									Class<?> fieldType = efm.getField().getType();
									Method method = efm.getMethod();
									Map<String,Object> requestParamMap = param.getBodyParamMap();
									if(requestParamMap.containsKey(fieldName)){
										//如果请求参数中，有这个字段的值，就可以塞
										Object fieldValue = CastUtil.castType(requestParamMap.get(fieldName), fieldType);
										ReflectionUtil.invokeMethod(paramValue, method, fieldValue);
									}else{
										//检测是否必填
										if(required != null){
											if(CollectionUtil.isEmpty(required)){
												error.add(fieldName);
											}else if(required.contains(fieldName)){
												error.add(fieldName);
											}
										}
									}
								}
							}
							actionParamValueList.set(i, paramValue);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		if(CollectionUtil.isNotEmpty(error)){
			throw new RestException("参数异常，"+error.toString()+"未获取到值");
		}
		return true;
	}

	@Override
	public void doEnd(HttpServletRequest request, HttpServletResponse arg1, Param arg2, Handler arg3) {
	}

}
