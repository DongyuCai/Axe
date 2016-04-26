package org.easyweb4j.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.easyweb4j.bean.FormParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求处理 工具类
 * Created by CaiDongYu on 2016/4/26.
 */
public final class RequestUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);
	
	/**
	 * 检查action url是否合法
	 * 只支持字母、斜杠、数字、下划线、$符
	 * 以及占位变量符号{}
	 */
	public static boolean checkUrl(String path){
		Pattern reg = Pattern.compile("^[A-Za-z0-9_\\$\\{\\}/]*$");
		Matcher matcher = reg.matcher(path);
		return matcher.find();
	}
	
	/**
	 * 替换pathParam成占位符
	 */
	public static String castPathParam(String nodeName){
		nodeName = nodeName.replaceAll("\\{[A-Za-z0-9_\\$]*\\}", "?");
		return nodeName;
	}
	
	/**
	 * 格式化 url 路径
	 * a//b/ 格式化后 /a/b
	 */
	public static String formatUrl(String path){
		path = path.trim().replaceAll("//", "/");
        if(!path.startsWith("/")){
        	path = "/"+path;
        }
        if(path.endsWith("/")){
        	path = path.substring(0, path.length()-1);
        }
        return path;
	}
	
	public static List<FormParam> parseParameter(HttpServletRequest request){
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

    @SuppressWarnings("unchecked")
	public static List<FormParam> parsePayload(HttpServletRequest request)throws IOException{
        List<FormParam> formParamList = new ArrayList<>();
        String body = CodeUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if(StringUtil.isNotEmpty(body)){
            try {
                Map<String,Object> payLoad = JsonUtil.fromJson(body, Map.class);
                formParamList.addAll(payLoad.entrySet().stream().map(entry -> new FormParam(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
            } catch (Exception e){
            	LOGGER.error("read body to json failure,body is: "+body);
            }
        }
        return formParamList;
    }
}
