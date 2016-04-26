package org.smart4j.framework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.bean.FormParam;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.Request;
import org.smart4j.framework.util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请求助手类
 * Created by CaiDongYu on 2016/4/25.
 */
public class RequestHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHelper.class);

    public static Param createParam(HttpServletRequest request)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));
        return new Param(formParamList);
    }

    private static List<FormParam> parseParameterNames(HttpServletRequest request){
        List<FormParam> formParamList = new ArrayList<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if(ArrayUtil.isNotEmpty(fieldValues)){
                for(String fieldValue:fieldValues){
                    formParamList.add(new FormParam(fieldName,fieldValue));
                }
            }
        }
        return formParamList;
    }

    private static List<FormParam> parseInputStream(HttpServletRequest request)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        String body = CodeUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if(StringUtil.isNotEmpty(body)){
            try {
                Map<String,Object> payLoad = JsonUtil.fromJson(body, Map.class);
                formParamList.addAll(payLoad.entrySet().stream().map(entry -> new FormParam(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
            } catch (Exception e){}
        }
        return formParamList;
    }
}
