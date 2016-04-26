package org.easyweb4j.helper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.easyweb4j.annotation.Request;
import org.easyweb4j.bean.Handler;
import org.easyweb4j.util.ArrayUtil;
import org.easyweb4j.util.CollectionUtil;
import org.easyweb4j.util.RequestUtil;

/**
 * Controller 助手类
 * 组合请求与处理的映射关系
 * Created by CaiDongYu on 2016/4/11.
 */
public final class ControllerHelper {

    /**
     * 存放映射关系 Action Map
     */
    public static final Map<String, Object> ACTION_MAP = new HashMap<>();

    static {
        //获取所有的 Controller 类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            for (Class<?> controllerClass : controllerClassSet) {
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {
                    for (Method method : methods) {
                        //判断方法是否带有 Action 注解
                        if (method.isAnnotationPresent(Request.class)) {
                        	Request action = method.getAnnotation(Request.class);
                            String mappingPath = action.value();
                            String requestMethod = action.method().REQUEST_METHOD;
                            
                            //检查mappingPath是否合规
                            if(!RequestUtil.checkUrl(mappingPath)){
                            	throw new RuntimeException("invalid @Request.value ["+mappingPath+"] of action: "+method);
                            }
                            
                            //格式化
                            mappingPath = RequestUtil.formatUrl(mappingPath);
                            
                            String[] nodeNames = mappingPath.split("/");
                            String nodeName = "";
                            String[] subNodeNameLineAry = {};
                            if(nodeNames.length > 0){
                            	nodeName = nodeNames[0];
                            	subNodeNameLineAry = new String[nodeNames.length-1];
                            	for(int i=0;i<subNodeNameLineAry.length;i++){
                            		subNodeNameLineAry[i] = nodeNames[i+1];
                            	}
                            }
                            generateActionMap(nodeName, ACTION_MAP, controllerClass, method, subNodeNameLineAry,requestMethod);
                        }
                    }
                }
            }
        }
    }

    
    @SuppressWarnings("unchecked")
	private static void generateActionMap(String nodeName,Map<String,Object> node,Class<?> controllerClass,Method method,String[] subNodeNameLineAry,String requestMethod){
    	//如果nodeName中有pathParam，全部替换成占位符?
    	nodeName = RequestUtil.castPathParam(nodeName);
    	
    	if(subNodeNameLineAry.length > 0){
    		Object nodeValue = null;
    		if(node.containsKey(nodeName)){
    			nodeValue = node.get(nodeName);
    		}else{
    			nodeValue = new HashMap<String,Object>();
    			node.put(nodeName, nodeValue);
    		}
        	String nodeName_next = subNodeNameLineAry[0];
        	String[] subNodeNameLineAry_next = new String[subNodeNameLineAry.length-1];
        	for(int i=0;i<subNodeNameLineAry_next.length;i++){
        		subNodeNameLineAry_next[i] = subNodeNameLineAry[i+1];
        	}
    		generateActionMap(nodeName_next, (Map<String,Object>)nodeValue, controllerClass, method, subNodeNameLineAry_next, requestMethod);
    	}else{
    		//到最后了
    		nodeName = requestMethod+":"+nodeName;
    		if(!node.containsKey(nodeName)){
    			Handler handler = new Handler(controllerClass, method);
    			node.put(nodeName, handler);
    		}else{
    			Handler handler = (Handler)node.get(nodeName);
    			throw new RuntimeException("find two same action: "+method+" === "+handler.getActionMethod());
    		}
    	}
    }
    
    /**
     * 获取 Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
    	requestMethod = requestMethod.toUpperCase();
    	
    	requestPath = RequestUtil.formatUrl(requestPath);
    	
    	String[] nodeNames = requestPath.split("/");
        String nodeName = "";
        String[] subNodeNameLineAry = {};
        if(nodeNames.length > 0){
        	nodeName = nodeNames[0];
        	subNodeNameLineAry = new String[nodeNames.length-1];
        	for(int i=0;i<subNodeNameLineAry.length;i++){
        		subNodeNameLineAry[i] = nodeNames[i+1];
        	}
        }
        
    	return findNodeFromActionMap(nodeName, ACTION_MAP, subNodeNameLineAry, requestMethod);
    }
    
    @SuppressWarnings("unchecked")
	private static Handler findNodeFromActionMap(String nodeName,Map<String,Object> node,String[] subNodeNameLineAry,String requestMethod){
    	//TODO:nodeName中占位符与请求的匹配
    	
    	if(subNodeNameLineAry.length > 0){
    		Object nodeValue = null;
    		if(node.containsKey(nodeName)){
    			nodeValue = node.get(nodeName);
    			String nodeName_next = subNodeNameLineAry[0];
            	String[] subNodeNameLineAry_next = new String[subNodeNameLineAry.length-1];
            	for(int i=0;i<subNodeNameLineAry_next.length;i++){
            		subNodeNameLineAry_next[i] = subNodeNameLineAry[i+1];
            	}
            	return findNodeFromActionMap(nodeName_next, (Map<String,Object>)nodeValue, subNodeNameLineAry_next, requestMethod);
    		}else{
    			//没找到叶子节点就已经找不到了
    			return null;
    		}
    	}else{
    		nodeName = requestMethod+":"+nodeName;
    		if(node.containsKey(nodeName)){
    			return (Handler)node.get(nodeName);
    		}else{
    			return null;
    		}
    	}
    }
    
}
