package org.axe.helper.mvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Param;
import org.axe.util.JsonUtil;
import org.axe.util.RequestUtil;
import org.axe.util.StreamUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求助手类
 * Created by CaiDongYu on 2016/4/25.
 */
public final class AjaxRequestHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(AjaxRequestHelper.class);
	
    @SuppressWarnings("unchecked")
	public static void initParam(Param param,HttpServletRequest request,String requestPath,String mappingPath)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(RequestUtil.parseParameter(request,requestPath,mappingPath));
//        String body = CodeUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        String body = StreamUtil.getString(request.getInputStream());
        Map<String,Object> bodyParamMap = null;
        if(StringUtil.isNotEmpty(body)){
            try {
            	if(StringUtil.isNotEmpty(body)){
            		if(body.startsWith("{") && body.endsWith("}")){
            			bodyParamMap = JsonUtil.fromJson(body, Map.class);
            		}
            		if(body.startsWith("[") && body.endsWith("]")){
            			List<Object> list = JsonUtil.fromJson(body, List.class);
            			bodyParamMap = new HashMap<String,Object>();
            			bodyParamMap.put("list", list);
            		}
            	}
            } catch (Exception e){
            	LOGGER.error("read body to json failure,body is: "+body);
            }
        }
        param.init(body,formParamList,null,bodyParamMap);
    }
}
