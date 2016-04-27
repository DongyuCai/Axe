package org.easyweb4j.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                            if(!RequestUtil.checkMappingPath(mappingPath)){
                            	throw new RuntimeException("invalid @Request.value ["+mappingPath+"] of action: "+method);
                            }
                            
                            //检查actionMethod是否合规
                            if(!RequestUtil.checkActionMethod(method)){
                            	throw new RuntimeException("action method 不允许使用 primitive 类型参数: "+method);
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
                            generateActionMap(nodeName, ACTION_MAP, controllerClass, method, subNodeNameLineAry,requestMethod,mappingPath);
                        }
                    }
                }
            }
        }
    }

    
    @SuppressWarnings("unchecked")
	private static void generateActionMap(String nodeName,Map<String,Object> node,Class<?> controllerClass,Method actionMethod,String[] subNodeNameLineAry,String requestMethod,String mappingPath){
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
    		generateActionMap(nodeName_next, (Map<String,Object>)nodeValue, controllerClass, actionMethod, subNodeNameLineAry_next, requestMethod, mappingPath);
    	}else{
    		//到最后了
    		nodeName = requestMethod+":"+nodeName;
    		if(!node.containsKey(nodeName)){
    			Handler handler = new Handler(controllerClass, actionMethod,requestMethod,mappingPath);
    			node.put(nodeName, handler);
    		}else{
    			Handler handler = (Handler)node.get(nodeName);
    			throw new RuntimeException("find two same action: "+actionMethod+" === "+handler.getActionMethod());
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
    	if(subNodeNameLineAry.length > 0){
    		//TODO:这里将压力放在了每次的请求解析，但愿节点下的分支不多，还不至于影响性能
    		nodeName = findNodeName(nodeName, node);
    		Object nodeValue = null;
    		if(nodeName != null){//此处必能用StringUtil.isEmpty，因为""空串是根节点
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
    		nodeName = findNodeName(nodeName, node);
    		if(nodeName != null){
    			return (Handler)node.get(nodeName);
    		}else{
    			return null;
    		}
    	}
    }
    
    
    private static String findNodeName(String nodeName,Map<String,Object> node){
    	boolean findNode = false;
		List<String> pathParamNodeNameList = new ArrayList<>();
    	for(String subNodeName:node.keySet()){
    		if(!RequestUtil.containsPathParam(subNodeName)){
    			if(subNodeName.equals(nodeName)){
    				findNode = true;
    				break;
    			}
    		}else{
    			//拥有pathParam的节点，放在固定字符串之后比对
    			pathParamNodeNameList.add(subNodeName);
    		}
    	}
    	if(!findNode){
    		//如果固定字符串的subNode里没有找到节点，就匹配路径参数
    		for(String pathParamNodeName:pathParamNodeNameList){
    			if(RequestUtil.compareNodeNameAndPathParamNodeName(nodeName, pathParamNodeName)){
    				findNode = true;
    				nodeName = pathParamNodeName;
    				break;
    			}
    		}
    	}
    	
    	return findNode?nodeName:null;
    }
}
