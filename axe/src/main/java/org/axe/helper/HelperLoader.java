package org.axe.helper;

import javax.servlet.ServletContext;

import org.axe.annotation.persistence.Dao;
import org.axe.helper.aop.AopHelper;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.base.FrameworkStatusHelper;
import org.axe.helper.base.MailHelper;
import org.axe.helper.ioc.BeanHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.helper.ioc.IocHelper;
import org.axe.helper.mvc.ControllerHelper;
import org.axe.helper.mvc.FilterHelper;
import org.axe.helper.mvc.FormRequestHelper;
import org.axe.helper.mvc.InterceptorHelper;
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.helper.persistence.DataSourceHelper;
import org.axe.helper.persistence.TableHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加载并初始化 Helper 类
 * Created by CaiDongYu on 2016/4/11.
 */
public final class HelperLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(HelperLoader.class);
	
	private static Helper[] helpers;
	
    public static void init(){
        init(null);
    }
    public static void init(ServletContext servletContext){
    	initHelpersAry();
    	refresHelpers(servletContext);
    }
    
    
    private static synchronized void initHelpersAry(){
    	helpers = new Helper[]{
    			new ConfigHelper(),//基础配置初始化
        		new FrameworkStatusHelper(),//框架基础信息初始化
                new ClassHelper(),//加载package下所有class到CLASS_SET
                new BeanHelper(),//实例化CLASS_SET里的类，放到BEAN_MAP里
                new FilterHelper(),//实例化所有Filter链表，并按层级排好序
                new InterceptorHelper(),//实例化所有Interceptor Map，interceptor没有顺序
                new AopHelper(),//针对有代理的类，实例化代理并替换掉BEAN_MAP里class原本的实例
                new IocHelper(),//组装所有@Autowired
                new ControllerHelper(),//加载ACTION_MAP
                new TableHelper(),//加载所有的@Table
                new DataSourceHelper(),//加载DataSource配置
                new DataBaseHelper(),//初始化数据库配置
                new MailHelper()//初始化邮件助手的配置
        };
    }
    
    public static synchronized void refresHelpers(ServletContext servletContext){
    	for (Helper helper:helpers){
    		helper.init();
        }
        
        //特别初始化
        if(servletContext != null){//因为表单请求可能带有文件上传，需要初始化Servlet相关设置
            FormRequestHelper.init(servletContext);
        }
        
        //装载的类日志分析
        //TODO:详细日志
        LOGGER.debug("Filter \tx"+FilterHelper.getSortedFilterList().size());
        LOGGER.debug("Interceptor \tx"+InterceptorHelper.getInterceptorMap().size());
        LOGGER.debug("Controller \tx"+ClassHelper.getControllerClassSet().size());
        LOGGER.debug("Action \tx"+ControllerHelper.getActionList().size());
        LOGGER.debug("Service \tx"+ClassHelper.getServiceClassSet().size());
        LOGGER.debug("Dao \tx"+ClassHelper.getClassSetByAnnotation(Dao.class).size());
        LOGGER.debug("Table \tx"+TableHelper.getEntityClassMap().size());
        LOGGER.debug("Axe system started success!");
        
        //释放ClassHelper占用的内存
        //TODO:(ok)目前来看，框架自身只有加载91个资源，并不很多
        ClassHelper.release();
    }
}
