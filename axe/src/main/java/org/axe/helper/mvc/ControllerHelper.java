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
package org.axe.helper.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.UnFuckOff;
import org.axe.bean.mvc.Handler;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.mvc.Filter;
import org.axe.interface_.mvc.Interceptor;
import org.axe.util.ArrayUtil;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.RequestUtil;

/**
 * Controller 助手类
 * 组合请求与处理的映射关系
 * @author CaiDongyu on 2016/4/11.
 */
public final class ControllerHelper implements Helper{

	/**
	 * Action List
	 * 因为框架不会用到这个变量，记录这份原始数据列表，是为了以便开发者使用
	 * 所以用LinkedList 为了合理利用点内存嘛
	 */
	private static List<Handler> ACTION_LIST;
    /**
     * 存放映射关系 Action Map
     * Action Map是Action List整理之后的树关系
     */
	private static Map<String, Object> ACTION_MAP;

	@Override
	public void init() throws Exception{
		synchronized (this) {
			ACTION_LIST = new LinkedList<>();
			ACTION_MAP = new HashMap<>();
			//获取所有的 Controller 类
	        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
	        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
	            for (Class<?> controllerClass : controllerClassSet) {
	            	Controller controller = controllerClass.getAnnotation(Controller.class);
	                Method[] actionMethods = controllerClass.getDeclaredMethods();
	                if (ArrayUtil.isNotEmpty(actionMethods)) {
	                    for (Method actionMethod : actionMethods) {
	                        //判断方法是否带有 Action 注解
	                        if (actionMethod.isAnnotationPresent(Request.class)) {
	                        	Request action = actionMethod.getAnnotation(Request.class);
	                            String mappingPath = controller.basePath()+"/"+action.path();
	                            String requestMethod = action.method().REQUEST_METHOD;
	                            
	                            //检查mappingPath是否合规
	                            if(!RequestUtil.checkMappingPath(mappingPath)){
	                            	throw new Exception("invalid @Request.value ["+mappingPath+"] of action: "+actionMethod.getName());
	                            }
	                            
	                            //检查actionMethod是否合规
	                            RequestUtil.checkActionMethod(actionMethod);
	                            
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
	                            generateActionMap(nodeName, ACTION_MAP, controllerClass,controller.desc(), actionMethod,action.desc(), subNodeNameLineAry,requestMethod,mappingPath);
	                        }
	                    }
	                }
	            }
	        }
	        
	        //ACTION_LIST排序
	        Collections.sort(ACTION_LIST, new Comparator<Handler>() {
				@Override
				public int compare(Handler o1, Handler o2) {
					return o1.getMappingPath().compareTo(o2.getMappingPath());
				}
			});
		}
	}

    
    @SuppressWarnings("unchecked")
	private static void generateActionMap(String nodeName,Map<String,Object> node,Class<?> controllerClass,String controllerDesc,Method actionMethod,String actionDesc,String[] subNodeNameLineAry,String requestMethod,String mappingPath){
    	//如果nodeName中有pathParam，全部替换成占位符?
    	nodeName = RequestUtil.castPathParam(nodeName);
    	
    	if(subNodeNameLineAry.length > 0){
    		Map<String,Object> nodeValue = null;
    		if(node.containsKey(nodeName)){
    			nodeValue = (Map<String,Object>)node.get(nodeName);
    		}else{
    			nodeValue = new HashMap<String,Object>();
    			node.put(nodeName, nodeValue);
    		}
        	String nodeName_next = subNodeNameLineAry[0];
        	String[] subNodeNameLineAry_next = new String[subNodeNameLineAry.length-1];
        	for(int i=0;i<subNodeNameLineAry_next.length;i++){
        		subNodeNameLineAry_next[i] = subNodeNameLineAry[i+1];
        	}
    		generateActionMap(nodeName_next, nodeValue, controllerClass, controllerDesc, actionMethod, actionDesc, subNodeNameLineAry_next, requestMethod, mappingPath);
    	}else{
    		//到最后了
    		nodeName = requestMethod+":"+nodeName;
    		if(!node.containsKey(nodeName)){
    			//##Filter 链
    			List<Filter> filterList = new ArrayList<>();
    			for(Filter filter:FilterHelper.getSortedFilterList()){
    				//#判断，此filter是否不可排除
    				UnFuckOff unFuckOff = filter.getClass().getAnnotation(UnFuckOff.class);
    				
    				//#首先判断是否匹配mappingPath
    				if(filter.setMapping() == null){
						//throw new RuntimeException("invalid filter["+filter.getClass()+"] setMapping is null");
    					continue;
    				}
    				Matcher mappingPathMatcher = filter.setMapping().matcher(requestMethod+":"+mappingPath);
    				if(!mappingPathMatcher.find()) continue;
    				//还要判断是否在刨除的规则里，此规则可以为null，直接跳过
    				if(filter.setNotMapping() != null){
    					Matcher notMappingPathMatcher = filter.setNotMapping().matcher(requestMethod+":"+mappingPath);
    					if(notMappingPathMatcher.find()) continue;
    				}
    				
					//#其次，说明匹配上了，判断controller是否排除了这个Filter
    				if(controllerClass.isAnnotationPresent(FilterFuckOff.class)){
    					FilterFuckOff filterFuckOff = controllerClass.getAnnotation(FilterFuckOff.class);
    					boolean findFuckOffFilter = false;
    					if(filterFuckOff.value().length == 0){
    						//数组长度是0表示默认排除所有filter，此时如果filter是可排除的，那么就准许排除
    						//如果filter是不可排除的，按照默认原则，不报错，自动不排除
    						if(unFuckOff == null){
    							findFuckOffFilter = true;
    						}
    					}else{
    						for(Class<?> filterClass:filterFuckOff.value()){
        						if(ReflectionUtil.compareType(filter.getClass(), filterClass)){
        							//如果指明排除某个Filter，那么要看，这个filter是否可排除，如果不可排除，则报错提示
        							if(unFuckOff == null){
        								findFuckOffFilter = true;
            							break;
        							}else{
        								throw new RuntimeException("unFuckOff filter ["+filter.getClass()+"] of controller: "+controllerClass.getName());
        							}
        						}
        					}
    					}
    					
    					//从排除的filter中扣除
    					if(filterFuckOff.notFuckOff().length > 0){ 
    						for(Class<?> filterClass:filterFuckOff.notFuckOff()){
        						if(ReflectionUtil.compareType(filter.getClass(), filterClass)){
        							findFuckOffFilter = false;
        							break;
        						}
        					}
    					}
    					
    					
    					
    					if(findFuckOffFilter) continue;
    				}
    				
    				//最后，说明Controller上没有排除此Filter，需要判断方法上是否排除
    				if(actionMethod.isAnnotationPresent(FilterFuckOff.class)){
    					FilterFuckOff filterFuckOff = actionMethod.getAnnotation(FilterFuckOff.class);
    					boolean findFuckOffFilter = false;
    					if(filterFuckOff.value().length == 0){ 
    						//数组长度是0表示默认排除所有filter，此时如果filter是可排除的，那么就准许排除
    						//如果filter是不可排除的，按照默认原则，不报错，自动不排除
    						if(unFuckOff == null){
    							findFuckOffFilter = true;
    						}
    					}else{
    						for(Class<?> filterClass:filterFuckOff.value()){
	    						if(ReflectionUtil.compareType(filter.getClass(), filterClass)){
	    							//如果指明排除某个Filter，那么要看，这个filter是否可排除，如果不可排除，则报错提示
        							if(unFuckOff == null){
        								findFuckOffFilter = true;
            							break;
        							}else{
        								throw new RuntimeException("unFuckOff filter ["+filter.getClass()+"] of controller: "+actionMethod.toGenericString());
        							}
	    						}
    						}
    					}
    					
    					if(filterFuckOff.notFuckOff().length > 0){ 
        					for(Class<?> filterClass:filterFuckOff.notFuckOff()){
	    						if(ReflectionUtil.compareType(filter.getClass(), filterClass)){
	    							findFuckOffFilter = false;
	    							break;
	    						}
	    					}
    					}
    					
    					if(findFuckOffFilter) continue;
    				}
    				
    				//到这里，说明此过滤器匹配上了这个actionMethod，而且没被排除
    				filterList.add(filter);
    			}
    			//##Interceptor 列表
    			Set<Class<? extends Interceptor>> interceptorSet = new HashSet<>();
    			List<Interceptor> interceptorList = new ArrayList<>();
    			//#controller上指定的拦截器
    			if(controllerClass.isAnnotationPresent(org.axe.annotation.mvc.Interceptor.class)){
    				org.axe.annotation.mvc.Interceptor interceptorAnnotation = controllerClass.getAnnotation(org.axe.annotation.mvc.Interceptor.class);
    				Class<? extends Interceptor>[] interceptorClassAry = interceptorAnnotation.value();
    				Map<Class<? extends Interceptor>, Interceptor> INTERCEPTOR_MAP = InterceptorHelper.getInterceptorMap();
    				if(interceptorClassAry != null){
    					for(Class<? extends Interceptor> interceptorClass:interceptorClassAry){
    						if(INTERCEPTOR_MAP.containsKey(interceptorClass)){
    							if(!interceptorSet.contains(interceptorClass)){
    								interceptorSet.add(interceptorClass);
    								interceptorList.add(INTERCEPTOR_MAP.get(interceptorClass));
    							}
    						}
    					}
    				}
    			}
    			//#action method上指定的拦截器，注意去重
    			if(actionMethod.isAnnotationPresent(org.axe.annotation.mvc.Interceptor.class)){
    				org.axe.annotation.mvc.Interceptor interceptorAnnotation = actionMethod.getAnnotation(org.axe.annotation.mvc.Interceptor.class);
    				Class<? extends Interceptor>[] interceptorClassAry = interceptorAnnotation.value();
    				Map<Class<? extends Interceptor>, Interceptor> INTERCEPTOR_MAP = InterceptorHelper.getInterceptorMap();
    				if(interceptorClassAry != null){
    					for(Class<? extends Interceptor> interceptorClass:interceptorClassAry){
    						if(INTERCEPTOR_MAP.containsKey(interceptorClass)){
    							if(!interceptorSet.contains(interceptorClass)){
    								interceptorSet.add(interceptorClass);
    								interceptorList.add(INTERCEPTOR_MAP.get(interceptorClass));
    								
    							}
    						}
    					}
    				}
    			}
    			Handler handler = new Handler(requestMethod,mappingPath,controllerClass,controllerDesc, actionMethod,actionDesc,filterList,interceptorList);
    			//构建树
    			node.put(nodeName, handler);
    			//存根到ACTIN_LIST
    			ACTION_LIST.add(handler);
    		}else{
    			Handler handler = (Handler)node.get(nodeName);
    			throw new RuntimeException("find the same action: "+actionMethod.toGenericString()+" === "+handler.getActionMethod().toGenericString());
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
    
    public static Map<String, Object> getActionMap() {
		return ACTION_MAP;
	}
    
    public static List<Handler> getActionList() {
		return ACTION_LIST;
	}


	@Override
	public void onStartUp() throws Exception {}
}
