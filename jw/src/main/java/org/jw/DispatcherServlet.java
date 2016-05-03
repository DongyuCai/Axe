package org.jw;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

import org.jw.annotation.RequestParam;
import org.jw.bean.Data;
import org.jw.bean.FileParam;
import org.jw.bean.FormParam;
import org.jw.bean.Handler;
import org.jw.bean.Param;
import org.jw.bean.View;
import org.jw.exception.RestException;
import org.jw.filter.Filter;
import org.jw.helper.AjaxRequestHelper;
import org.jw.helper.BeanHelper;
import org.jw.helper.ClassHelper;
import org.jw.helper.ConfigHelper;
import org.jw.helper.ControllerHelper;
import org.jw.helper.FormRequestHelper;
import org.jw.util.CastUtil;
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
        
        LOGGER.debug("jw framework started success!");
        LOGGER.debug("controllers\tx"+ClassHelper.getControllerClassSet().size());
        LOGGER.debug("services\tx"+ClassHelper.getServiceClassSet().size());
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
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
                for(Filter filter:filterList){
                	doFilterSuccess = filter.doFilter(request, response, param, handler);
                	if(!doFilterSuccess) break;
                }
                if(doFilterSuccess){
                	//调用 Action方法
                	Method actionMethod = handler.getActionMethod();
                	Object result = this.invokeActionMethod(controllerBean, actionMethod, param, request, response);
                	
                	if(result instanceof View){
                		handleViewResult((View)result,request,response);
                	} else if (result instanceof Data){
                		handleDataResult((Data)result,response);
                	}
                }
            }else{
            	//404
    			throw new RestException(RestException.SC_NOT_FOUND, "404 Not Found");
            }
		} catch (RestException e){
			writeBackToClient(e.getStatus(), e.getMessage(), response);
		} catch (Exception e) {
			//500
			writeBackToClient(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "500 server error", response);
		}
    }
    
    public void writeBackToClient(int status,String msg,HttpServletResponse response){
    	try {
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
    	Parameter[] parameterAry = actionMethod.getParameters();
    	parameterAry = parameterAry == null?new Parameter[0]:parameterAry;
    	Class<?>[] parameterTypes = actionMethod.getParameterTypes();
    	parameterTypes = parameterTypes == null?new Class<?>[0]:parameterTypes;
    	//按顺序来，塞值
    	List<Object> parameterValueList = new ArrayList<>();
    	for(int i=0;i<parameterAry.length && parameterAry.length == parameterTypes.length;i++){
    		Object parameterValue = null;
    		do{
    			Parameter parameter = parameterAry[i];
    			Class<?> parameterType = parameterTypes[i];
    			
    			//#是否@RequestParam标注的
    			if(parameter.isAnnotationPresent(RequestParam.class)){
    				String fieldName = parameter.getAnnotation(RequestParam.class).value();
    				if(parameterType.isArray() 
    						&& 
    						ReflectionUtil.compareType(parameterType.getComponentType(), FileParam.class)
    						){
    					//如果是 FileParam[]这样的
    					Map<String,List<FileParam>> fileMap = param.getFileMap();
    					if(fileMap.containsKey(fieldName)){
    						List<FileParam> fileParamList = fileMap.get(fieldName);
    						if(CollectionUtil.isNotEmpty(fileParamList)){
    							FileParam[] fileParamAry= null;
    							fileParamAry = new FileParam[0];
    							fileParamAry = fileParamList.toArray(fileParamAry);
    							parameterValue = fileParamAry;
    						}
    					}
    				}else if(ReflectionUtil.compareType(parameterType,FileParam.class)){
    					//单文件
    					Map<String,List<FileParam>> fileMap = param.getFileMap();
    					if(fileMap.containsKey(fieldName)){
    						List<FileParam> fileParamList = fileMap.get(fieldName);
    						if(CollectionUtil.isNotEmpty(fileParamList)){
    							parameterValue = fileParamList.get(fileParamList.size()-1);
    						}
    					}
    				}else{
    					//TODO:除了文件数组、单文件比较特殊需要转换，其他的都按照自动类型匹配，这样不够智能
    					//而且，如果fieldMap和fileMap出现同名，则会导致参数混乱，不支持同名（虽然这种情况说明代码写的真操蛋！）
    					Map<String,List<FormParam>> fieldMap = param.getFieldMap();
    					Map<String,List<FileParam>> fileMap = param.getFileMap();
    					if(fieldMap.containsKey(fieldName)){
    						List<FormParam> formParamList = fieldMap.get(fieldName);
    						parameterValue = CastUtil.smartCast(formParamList, parameterType);
    					}else if(fileMap.containsKey(fieldName)){
    						parameterValue = fileMap.get(fieldName);
    					}
    				}
    				break;
    			}
    			//#不含注解的
    			//如果是HttpServletRequest
    			if(ReflectionUtil.compareType(HttpServletRequest.class, parameterType)){
    				parameterValue = request;
    				break;
    			}
    			if(ReflectionUtil.compareType(HttpServletResponse.class, parameterType)){
    				parameterValue = response;
    				break;
    			}
    			//如果是Param
    			if(ReflectionUtil.compareType(Param.class,parameterType)){
    				parameterValue = param;
    				break;
    			}
    			
    			//#其他杂七杂八类型，只能给null，框架不管
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

    private void handleDataResult(Data data,HttpServletResponse response) throws IOException{
        //返回JSON数据
        Object model = data.getModel();
        if(model != null){
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }
}
