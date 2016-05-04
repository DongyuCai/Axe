package org.jw;

import org.jw.annotation.Dao;
import org.jw.helper.*;
import org.jw.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * 加载并初始化 Helper 类
 * Created by CaiDongYu on 2016/4/11.
 */
public final class HelperLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(HelperLoader.class);
	
	
    public static void init(){
        init(null);
    }
    public static void init(ServletContext servletContext){
        Class<?>[] classList = {
                ClassHelper.class,//加载package下所有class到CLASS_SET
                BeanHelper.class,//实例化CLASS_SET里的类，放到BEAN_MAP里
                FilterHelper.class,//实例化所有Filter，并按层级排好序
                AopHelper.class,//针对有代理的类，实例化代理并替换掉BEAN_MAP里class原本的实例
                IocHelper.class,
                ControllerHelper.class,
                DataBaseHelper.class
        };
        for (Class<?> cls:classList){
            ClassUtil.loadClass(cls.getName(),true);
        }
        
        //特别初始化
        if(servletContext != null){//因为表单请求可能带有文件上传，需要初始化Servlet相关设置
            FormRequestHelper.init(servletContext);
        }
        
        //装载的类日志分析
        LOGGER.error("Filter implements\tx"+ClassHelper.getFilterClassSet().size());
        LOGGER.error("@Controllers :\tx"+ClassHelper.getControllerClassSet().size());
        LOGGER.error("@Service :\tx"+ClassHelper.getServiceClassSet().size());
        LOGGER.error("@Dao :\tx"+ClassHelper.getClassSetByAnnotation(Dao.class).size());
        LOGGER.error("@Table :\tx"+DataBaseHelper.getEntityClassMap().size());
        LOGGER.error("jw framework started success!");
    }
}
