package org.easyweb4j.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.easyweb4j.bean.BodyParam;
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
	 * 允许接受的字符串种类
	 * 字母、数字、下划线、$
	 */
	private static final String REG_WORD = "A-Za-z0-9_\\$";
	
	/**
	 * 检查action url是否合法
	 * 只支持字母、斜杠、数字、下划线、$符
	 * 以及占位变量符号{}
	 * 占位符花括号不可以紧挨着，否则语意不明
	 */
	public static boolean checkMappingPath(String path){
		do{
			Pattern reg = Pattern.compile("^["+REG_WORD+"\\{\\}/]*$");
			Matcher matcher = reg.matcher(path);
			if(!matcher.find()) return false;
			
			path = castPathParam(path);
			if(path.contains("??")) return false;
		}while(false);
		return true;
	}
	
	/**
	 * 检查ActionMethod是否合规
	 * 不准包含基本类型
	 * 基本类型指： int、short、long、double、float、boolean、char等
	 */
	public static boolean checkActionMethod(Method actionMethod){
    	Class<?>[] parameterTypeAry = actionMethod.getParameterTypes();
    	parameterTypeAry = parameterTypeAry == null?new Class<?>[0]:parameterTypeAry;
    	for(Class<?> parameterType:parameterTypeAry){
			if(parameterType.isPrimitive()){
				return false;
			}
    	}
    	return true;
	}

	/**
	 * 替换pathParam成占位符 ?
	 */
	public static String castPathParam(String nodeName){
		nodeName = nodeName.replaceAll("\\{["+REG_WORD+"]*\\}", "?");
		return nodeName;
	}
	
	/**
	 * 查看 ACTION_MAP中的nodeName，是不是包含占位符的
	 */
	public static boolean containsPathParam(String nodeName){
		return nodeName.contains("?");
	}
	
	/**
	 * 比较请求的nodeName和action_map中带参的pathParamNodeName是否可以匹配
	 */
	public static boolean compareNodeNameAndPathParamNodeName(String nodeName,String pathParamNodeName){
		pathParamNodeName = pathParamNodeName.replaceAll("\\?", "["+REG_WORD+"]*");
		pathParamNodeName = "^"+pathParamNodeName+"$";
		Pattern reg = Pattern.compile(pathParamNodeName);
		Matcher matcher = reg.matcher(nodeName);
		return matcher.find();
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
	
	public static List<FormParam> parseParameter(HttpServletRequest request,String requestPath,String mappingPath){
    	List<FormParam> formParamList = new ArrayList<>();
        //分析url查询字符串
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
        //分析url路径参数
        //requestPath 客户端过来的servletPath
        //action方法上的@Quest.value注解值
        Pattern reg4requestPath = Pattern.compile(mappingPath.replaceAll("\\{["+REG_WORD+"]*\\}","(["+REG_WORD+"]*)"));
        Matcher matcher4requestPath = reg4requestPath.matcher(requestPath);
		Pattern reg4mappingPath = Pattern.compile("\\{(["+REG_WORD+"]*)\\}");
		Matcher matcher4mappingPath = reg4mappingPath.matcher(mappingPath);
		boolean fieldValueFind = matcher4requestPath.find();
		for(int fieldValueGroupIndex = 1; matcher4mappingPath.find();fieldValueGroupIndex++){
			String fieldName = matcher4mappingPath.group(1);
			String fieldValue = null;
			if(fieldValueFind && fieldValueGroupIndex<=matcher4requestPath.groupCount()){
				fieldValue = matcher4requestPath.group(fieldValueGroupIndex);
			}
			formParamList.add(new FormParam(fieldName,fieldValue));
		}
		
        return formParamList;
    }

    @SuppressWarnings("unchecked")
	public static List<BodyParam> parsePayload(HttpServletRequest request)throws IOException{
        List<BodyParam> formParamList = new ArrayList<>();
        String body = CodeUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if(StringUtil.isNotEmpty(body)){
            try {
                Map<String,Object> payLoad = JsonUtil.fromJson(body, Map.class);
                formParamList.addAll(payLoad.entrySet().stream().map(entry -> new BodyParam(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
            } catch (Exception e){
            	LOGGER.error("read body to json failure,body is: "+body);
            }
        }
        return formParamList;
    }
    
    public static String getRequestMethod(HttpServletRequest request){
    	return request.getMethod().toUpperCase();
    }
    
    public static String getRequestPath(HttpServletRequest request){
    	return request.getServletPath();
    }
}
