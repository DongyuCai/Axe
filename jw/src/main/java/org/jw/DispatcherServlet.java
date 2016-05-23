package org.jw;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.annotation.mvc.RequestParam;
import org.jw.bean.mvc.Data;
import org.jw.bean.mvc.Handler;
import org.jw.bean.mvc.Param;
import org.jw.bean.mvc.View;
import org.jw.constant.CharacterEncoding;
import org.jw.constant.ContentType;
import org.jw.exception.RestException;
import org.jw.helper.base.ConfigHelper;
import org.jw.helper.ioc.BeanHelper;
import org.jw.helper.mvc.AjaxRequestHelper;
import org.jw.helper.mvc.ControllerHelper;
import org.jw.helper.mvc.FormRequestHelper;
import org.jw.interface_.mvc.Filter;
import org.jw.util.CollectionUtil;
import org.jw.util.JsonUtil;
import org.jw.util.ReflectionUtil;
import org.jw.util.RequestUtil;
import org.jw.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求转发器
 * Created by CaiDongYu on 2016/4/11.
 */
@WebServlet(urlPatterns = "/*" , loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);

	@Override
    public void init(ServletConfig servletConfig) throws ServletException{
        //获取 ServletContext 对象（用于注册servlet）
        ServletContext servletContext = servletConfig.getServletContext();
        //注册处理JSP的Servlet
        String appJspPath = ConfigHelper.getAppJspPath();
        if(StringUtil.isNotEmpty(appJspPath)){
        	appJspPath = appJspPath.endsWith("/") ? appJspPath : appJspPath+"/";
        	ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        	jspServlet.addMapping(appJspPath+"*");
        }
        //注册处理静态资源的默认Servlet
        String appAssetPath = ConfigHelper.getAppAssetPath();
        if(StringUtil.isNotEmpty(appAssetPath)){
        	appAssetPath = appAssetPath.endsWith("/") ? appAssetPath : appAssetPath+"/";
        	ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        	defaultServlet.addMapping(appAssetPath+"*");
        }

        //初始化框架相关 helper 类
        HelperLoader.init(servletContext);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
    	
    	String contentType = ContentType.APPLICATION_JSON;
    	String characterEncoding = CharacterEncoding.UTF_8;
        try {
        	//获取请求方法与请求路径
            String requestMethod = RequestUtil.getRequestMethod(request);
            String requestPath = RequestUtil.getRequestPath(request);

            if(requestPath != null && requestPath.equals("/favicon.ico")){
                return;
            }
            //获取 Action 处理器
            Handler handler = ControllerHelper.getHandler(requestMethod,requestPath);
            if(handler != null){
                //获取 Controller  类和 Bean 实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
                contentType = handler.getContentType();
                characterEncoding = handler.getCharacterEncoding();
                
                //创建你请求参数对象
                Param param;
                if(FormRequestHelper.isMultipart(request)){
                    //如果是文件上传
                    param = FormRequestHelper.createParam(request,requestPath,handler.getMappingPath());
                }else{
                    //如果不是
                    param = AjaxRequestHelper.createParam(request,requestPath,handler.getMappingPath());
                }
                
                //先执行Filter链
                List<Filter> filterList = handler.getFilterList();
                boolean doFilterSuccess = true;
                if(CollectionUtil.isNotEmpty(filterList)){
                	for(Filter filter:filterList){
                		doFilterSuccess = filter.doFilter(request, response, param, handler);
                		if(!doFilterSuccess) break;
                	}
                }
                if(doFilterSuccess){
                	//调用 Action方法
                	Method actionMethod = handler.getActionMethod();
                	Object result = this.invokeActionMethod(controllerBean, actionMethod, param, request, response);
                	if(result != null){
                		if(result instanceof View){
                			handleViewResult((View)result,request,response);
                		} else if (result instanceof Data){
                			handleDataResult((Data)result,response,handler);
                		} else {
                			Data data = new Data(result);
                			handleDataResult(data,response,handler);
                		}
                	}
                }
            }else{
            	//404
    			throw new RestException(RestException.SC_NOT_FOUND, "404 Not Found");
            }
		} catch (RestException e){
			writeError(e.getStatus(), e.getMessage(), response, contentType, characterEncoding);
		} catch (Exception e) {
			LOGGER.error("server error",e);
			//500
			writeError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "500 server error", response, contentType, characterEncoding);
		}
    }
    
    public void writeError(int status,String msg,HttpServletResponse response,String contentType,String characterEncoding){
    	try {
        	response.setContentType(contentType);
        	response.setCharacterEncoding(characterEncoding);
    		response.setStatus(status);
        	PrintWriter writer = response.getWriter();
        	writer.write(msg);
        	writer.flush();
        	writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("server error",e);
		}
    }

    private Object invokeActionMethod(Object controllerBean,Method actionMethod,Param param,HttpServletRequest request, HttpServletResponse response){
    	Object result;
    	Type[] parameterTypes = actionMethod.getGenericParameterTypes();
    	Annotation[][] parameterAnnotations = actionMethod.getParameterAnnotations();
    	parameterTypes = parameterTypes == null?new Class<?>[0]:parameterTypes;
    	//按顺序来，塞值
    	List<Object> parameterValueList = new ArrayList<>();
    	for(int i=0;i<parameterTypes.length;i++){
    		Object parameterValue = null;
    		do{
    			Type parameterType = parameterTypes[i];
    			Annotation[] parameterAnnotationAry = parameterAnnotations[i];
    			
    			RequestParam requestParam = null;
    			for(Annotation anno:parameterAnnotationAry){
    				if(anno instanceof RequestParam){
    					requestParam = (RequestParam)anno;
    					break;
    				}
    			}
    			
    			//## 是否@RequestParam标注的
    			if(requestParam != null){
    				String fieldName = requestParam.value();
					//TODO:除了文件数组、单文件比较特殊需要转换，其他的都按照自动类型匹配，这样不够智能
					//而且，如果fieldMap和fileMap出现同名，则会导致参数混乱，不支持同名（虽然这种情况说明代码写的真操蛋！）
					parameterValue = RequestUtil.getRequestParam(param,fieldName, parameterType);
    				break;
    			}else{
    				Class<?> parameterClass = null; 
    				if(parameterType instanceof Class){
    					parameterClass = (Class<?>)parameterType;
    				}else if(parameterType instanceof ParameterizedType){
    					parameterClass = (Class<?>)((ParameterizedType) parameterType).getRawType();
    				}
    				if(parameterClass != null){
    					//## 不含注解的
    					//* 如果是HttpServletRequest
    					if(ReflectionUtil.compareType(HttpServletRequest.class, parameterClass)){
    						parameterValue = request;
    						break;
    					}
    					if(ReflectionUtil.compareType(HttpServletResponse.class, parameterClass)){
    						parameterValue = response;
    						break;
    					}
    					//* 如果是Param
    					if(ReflectionUtil.compareType(Param.class,parameterClass)){
    						parameterValue = param;
    						break;
    					}
    					//* 如果是Map<String,Object> 
    					if(ReflectionUtil.compareType(Map.class, parameterClass)){
    						parameterValue = param.getBodyMap();
    						/*if(parameterizedType instanceof ParameterizedType){
    						Type[] actualTypes = ((ParameterizedType) parameterizedType).getActualTypeArguments();
    						if(actualTypes.length > 1){
    							Type mapKeyType = actualTypes[0];
    							Type mapValueType = actualTypes[1];
    							if(ReflectionUtil.compareType(String.class, (Class<?>)mapKeyType) && 
    									ReflectionUtil.compareType(Object.class, (Class<?>)mapValueType)){
    								//Map<String,Object> 只能接受到 payload就是body里的json对象
    								parameterValue = param.getBodyMap();
    							}
    						}
    					}else{
							//Map
							parameterValue = param.getBodyMap();
						}*/
    					}
    				}
    			}
    			
    			//## 其他杂七杂八类型，只能给null，框架不管
    		}while(false);
    		parameterValueList.add(parameterValue);
    	}
    	
        result = ReflectionUtil.invokeMethod(controllerBean,actionMethod,parameterValueList.toArray());
        return result;
    }
    
    private void handleViewResult(View view,HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        //返回JSP页面或者请求跳转
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)){
            //TODO:什么叫 startWith("/") 这样就认为是浏览器跳转了?
            if(path.startsWith("/")){
                response.sendRedirect(request.getContextPath()+path);
            }else{
                Map<String,Object> model = view.getModel();
                for(Map.Entry<String,Object> entry:model.entrySet()){
                    request.setAttribute(entry.getKey(),entry.getValue());
                }
                request.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(request,response);
            }
        }
    }

    private void handleDataResult(Data data,HttpServletResponse response,Handler handler) throws IOException{
    	response.setContentType(handler.getContentType());
    	response.setCharacterEncoding(handler.getCharacterEncoding());
        //返回JSON数据
        Object model = data.getModel();
        if(model != null){
            String json = JsonUtil.toJson(model);
            if(json != null){
            	PrintWriter writer = response.getWriter();
            	writer.write(json);
            	writer.flush();
            	writer.close();
            }
        }
    }
}
