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
package org.axe;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.bean.mvc.Data;
import org.axe.bean.mvc.ExceptionHolder;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.ResultHolder;
import org.axe.bean.mvc.View;
import org.axe.constant.CharacterEncoding;
import org.axe.constant.ContentType;
import org.axe.constant.RequestContent;
import org.axe.exception.RedirectorInterrupt;
import org.axe.exception.RestException;
import org.axe.helper.HelperLoader;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.base.MailHelper;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.mvc.ControllerHelper;
import org.axe.interface_.mvc.Filter;
import org.axe.interface_.mvc.Interceptor;
import org.axe.util.CollectionUtil;
import org.axe.util.JsonUtil;
import org.axe.util.LogUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.RequestUtil;
import org.axe.util.StringUtil;

/**
 * 请求转发器
 * @author CaiDongyu on 2016/4/11.
 */
//保留一个，就使用web.xml中的配置就行
//@WebServlet(urlPatterns = "/*" , loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	private static Boolean AXE_INITED = false;
	
	@Override
    public void init(ServletConfig servletConfig) throws ServletException{
		synchronized (AXE_INITED) {
			if(!AXE_INITED){
				AXE_INITED = true;
				//获取 ServletContext 对象（用于注册servlet）
		        ServletContext servletContext = servletConfig.getServletContext();

		        //初始化框架相关 helper 类
		        try {
					HelperLoader.init(servletContext);
					
					//注册处理JSP的Servlet
			        String appJspPath = ConfigHelper.getAppJspPath();
			        if(StringUtil.isNotEmpty(appJspPath)){
			        	appJspPath = appJspPath.endsWith("/") ? appJspPath : appJspPath+"/";
			        	ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
			        	if(jspServlet != null){
			        		jspServlet.addMapping(appJspPath+"*");
			        	}else{
			        		LogUtil.error(new Exception("unsupport jsp application! servletContext.getServletRegistration(\"jsp\") is null !"));
			        	}
			        }
			        //注册处理静态资源的默认Servlet
			        String appAssetPath = ConfigHelper.getAppAssetPath();
			        if(StringUtil.isNotEmpty(appAssetPath)){
			        	appAssetPath = appAssetPath.endsWith("/") ? appAssetPath : appAssetPath+"/";
			        	ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
			        	if(defaultServlet != null){
			        		defaultServlet.addMapping(appAssetPath+"*");
			        	}else{
			        		LogUtil.error(new Exception("unsupport static asset! servletContext.getServletRegistration(\"default\") is null !"));
			        	}
			        }
				} catch (Exception e) {
					LogUtil.error(e);
					System.exit(1);
				}
			}
		}
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
    	String contentType = ContentType.APPLICATION_JSON.CONTENT_TYPE;
    	String characterEncoding = CharacterEncoding.UTF_8.CHARACTER_ENCODING;
    	Stack<Filter> doEndFilterStack = null;
    	Stack<Interceptor> doEndInterceptorStack = null;
    	Integer RESPONSE_IS_USED = 0;
    	Param param = null;
    	Handler handler = null;
    	ResultHolder resultHolder = new ResultHolder();
    	ExceptionHolder exceptionHolder = new ExceptionHolder();
        try {
    		//清理请求上下文，避免线程复用导致上下文中存在脏数据
        	RequestContent.clean();
        	
        	//获取请求方法与请求路径
            String requestMethod = RequestUtil.getRequestMethod(request);
            String requestPath = RequestUtil.getRequestPath(request);

            /*if(requestPath != null && requestPath.equals("/favicon.ico")){
                return;
            }*/
            //获取 Action 处理器
            handler = ControllerHelper.getHandler(requestMethod,requestPath);
            if(handler != null){
            	//获取 Controller  类和 Bean 实例
            	Class<?> controllerClass = handler.getControllerClass();
            	Method actionMethod = handler.getActionMethod();
            	
                Object controllerBean = BeanHelper.getBean(controllerClass);
                contentType = handler.getContentType();
                characterEncoding = handler.getCharacterEncoding();
                
                //##1.创建你请求参数对象
                param = new Param(requestPath);
                
                //##2.先执行Filter链
                List<Filter> filterList = handler.getFilterList();
                boolean doFilterSuccess = true;
                if(CollectionUtil.isNotEmpty(filterList)){
                	for(Filter filter:filterList){
                		//被执行的Filter，都添加到end任务里
                		if(doEndFilterStack == null){
                			doEndFilterStack = new Stack<>();
                		}
                		doEndFilterStack.push(filter);
                		doFilterSuccess = filter.doFilter(request, response, param, handler);
                		//执行失败则跳出，不再往下进行
                		if(!doFilterSuccess) break;
                	}
                }
                //##3.执行Interceptor 列表
                List<Interceptor> interceptorList = handler.getInterceptorList();
                boolean doInterceptorSuccess = true;
                if(CollectionUtil.isNotEmpty(interceptorList)){
                	for(Interceptor interceptor:interceptorList){
                		//被执行的Interceptor，都添加到end任务里
                		if(doEndInterceptorStack == null){
                			doEndInterceptorStack = new Stack<>();
                		}
                		doEndInterceptorStack.push(interceptor);
                		doInterceptorSuccess = interceptor.doInterceptor(request, response, param, handler);
                		if(!doInterceptorSuccess) break;
                	}
                }
                //##4.执行action
                if(doFilterSuccess && doInterceptorSuccess){
                	//调用 Action方法
                	Object result = ReflectionUtil.invokeMethod(controllerBean,actionMethod,param.getActionParamList().toArray());
                	resultHolder.setResult(result);
                }
            }else{
            	//404
    			throw new RestException(RestException.SC_NOT_FOUND, "404 Not Found");
            }
		} catch (Exception e) {
			exceptionHolder.setException(e);
		} finally {
			if(RESPONSE_IS_USED>0){
				LogUtil.error(new Exception("RESPONSE_IS_USED:"+RESPONSE_IS_USED));
			}
			//##5.执行Filter链各个节点的收尾工作
			while(CollectionUtil.isNotEmpty(doEndFilterStack)){
				try {
					doEndFilterStack.pop().doEnd(request, response, param, handler,resultHolder,exceptionHolder);
				} catch (Exception endEx) {
					LogUtil.error(new Exception("filter doEnd failed"));
					LogUtil.error(endEx);
				}
			}
			//##6.执行Interceptor链各个节点的收尾工作
			while(CollectionUtil.isNotEmpty(doEndInterceptorStack)){
				try {
					doEndInterceptorStack.pop().doEnd(request, response, param, handler,resultHolder,exceptionHolder);
				} catch (Exception endEx) {
					LogUtil.error(endEx);
				}
			}
			
			
			if(exceptionHolder.getException() != null){
				//##6.异常处理
				if(exceptionHolder.getException() instanceof RedirectorInterrupt){
					//被中断，跳转
					try {
						RedirectorInterrupt e = (RedirectorInterrupt)(exceptionHolder.getException()); 
						handleViewResult(e.getView(),request,response,RESPONSE_IS_USED);
					} catch (Exception e1) {
						LogUtil.error(e1);
					}
				}else if(exceptionHolder.getException() instanceof RestException){
					try {
						RestException e = (RestException)(exceptionHolder.getException()); 
						//需要返回前台信息的异常
						writeError(e.getStatus(), e.getMessage(), response, RESPONSE_IS_USED, contentType, characterEncoding);
					} catch (Exception e1) {
						LogUtil.error(e1);
					}
				}else{
					//其他情况就是Exception 500
					LogUtil.error(exceptionHolder.getException());
					//500
					writeError(RestException.SC_INTERNAL_SERVER_ERROR, "500 server error,"+exceptionHolder.getException().getMessage(), response, RESPONSE_IS_USED, contentType, characterEncoding);
					
			    	try {
			    		//邮件通知
						MailHelper.errorMail(exceptionHolder.getException());
					} catch (Exception e1) {
						LogUtil.error(e1);
					}
				}
			}else{
				//##6.返回结果
				if(resultHolder.getResult() != null){
            		try {
            			if(resultHolder.getResult() instanceof View){
                			handleViewResult((View)(resultHolder.getResult()),request,response,RESPONSE_IS_USED);
                		} else if (resultHolder.getResult() instanceof Data){
                			handleDataResult((Data)(resultHolder.getResult()),response,handler,RESPONSE_IS_USED);
                		} else {
                			Data data = new Data(resultHolder.getResult());
                			handleDataResult(data,response,handler,RESPONSE_IS_USED);
                		}
					} catch (Exception e2) {
						LogUtil.error(e2);
						try {
				    		//邮件通知
							MailHelper.errorMail(e2);
						} catch (Exception e1) {
							LogUtil.error(e1);
						}
					}
            	}
			}

    		//清理请求上下文
        	RequestContent.clean();
		}
    }
    
    public void writeError(int status,String msg,HttpServletResponse response,Integer RESPONSE_IS_USED,String contentType,String characterEncoding){
    	if(RESPONSE_IS_USED == 0){
        	RESPONSE_IS_USED++;
    		try {
    			response.setContentType(contentType);
    			response.setCharacterEncoding(characterEncoding);
    			response.setStatus(status);
    			PrintWriter writer = response.getWriter();
    			writer.write(msg);
//    			writer.flush();
//    			writer.close();
    		} catch (Exception e) {
				LogUtil.error(e);
    		}
    	}
    }
    
    
    private void handleViewResult(View view,HttpServletRequest request,HttpServletResponse response,Integer RESPONSE_IS_USED) throws IOException,ServletException{
        if(RESPONSE_IS_USED == 0){
        	RESPONSE_IS_USED++;
        	//返回JSP页面或者请求跳转
            String path = view.getPath();
            if (StringUtil.isNotEmpty(path)){
                if(view.isRedirect()){
                	Map<String,Object> model = view.getModel();
                	if(CollectionUtil.isNotEmpty(model)){
                		if(path.contains("?")){
                			path = path+"&";
                		}else{
                			path = path+"?";
                		}
                        for(Map.Entry<String,Object> entry:model.entrySet()){
                        	path = path+entry.getKey()+"="+String.valueOf(entry.getValue())+"&";
                        }
                        if(path.endsWith("&")){
                        	path = path.substring(0, path.length()-1);
                        }
                	}
                	
                	if(view.isUri()){
                    	if(!path.startsWith("/")){
                    		path = "/"+path;
                    	}
                		response.sendRedirect(request.getContextPath()+path);
                	}else{
                		response.sendRedirect(path);
                	}
                }else{
                    Map<String,Object> model = view.getModel();
                    for(Map.Entry<String,Object> entry:model.entrySet()){
                        request.setAttribute(entry.getKey(),entry.getValue());
                    }
                	if(!path.startsWith("/")){
                		path = "/"+path;
                	}
                    request.getRequestDispatcher(path).forward(request,response);
                }
            }
        }
    }

    private void handleDataResult(Data data,HttpServletResponse response,Handler handler,Integer RESPONSE_IS_USED) throws IOException{
    	if(RESPONSE_IS_USED == 0){
        	RESPONSE_IS_USED++;
    		response.setContentType(handler.getContentType());
    		response.setCharacterEncoding(handler.getCharacterEncoding());
    		//返回JSON数据
    		Object model = data.getModel();
    		if(model != null){
    			String json = model instanceof String ? String.valueOf(model):JsonUtil.toJson(model);
    			if(json != null){
    				PrintWriter writer = response.getWriter();
    				writer.write(json);
//    				writer.flush();
//    				writer.close();
    			}
    		}
    	}
    }
}
