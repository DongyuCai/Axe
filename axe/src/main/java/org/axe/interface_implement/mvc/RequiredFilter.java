package org.axe.interface_implement.mvc;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.mvc.RequestParam;
import org.axe.annotation.mvc.Required;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Handler.ActionParam;
import org.axe.bean.mvc.Param;
import org.axe.exception.RestException;
import org.axe.interface_.mvc.Filter;
import org.axe.util.CollectionUtil;
import org.axe.util.StringUtil;

public abstract class RequiredFilter implements Filter {

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
					boolean require = false;
					String fieldName = null;
					for(Annotation at:ats){
						if(at instanceof RequestParam){
							fieldName = ((RequestParam)at).value();
						}
						if(at instanceof  Required){
							require = true;
						}
					}
					//必填
					if(require && StringUtil.isNotEmpty(fieldName)){
						if(paramValue == null){
							error.add(fieldName);
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
	public void doEnd(HttpServletRequest arg0, HttpServletResponse arg1, Param arg2, Handler arg3) {
	}

}
