package org.jw;

import javax.servlet.ServletContext;

import org.jw.annotation.Dao;
import org.jw.helper.AopHelper;
import org.jw.helper.BeanHelper;
import org.jw.helper.ClassHelper;
import org.jw.helper.ControllerHelper;
import org.jw.helper.DataBaseHelper;
import org.jw.helper.FilterHelper;
import org.jw.helper.FormRequestHelper;
import org.jw.helper.IocHelper;
import org.jw.helper.TableHelper;
import org.jw.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                IocHelper.class,//组装所有@Autowired
                ControllerHelper.class,//加载ACTION_MAP
                TableHelper.class,//加载所有的@Table
                DataBaseHelper.class//初始化数据库配置
        };
        for (Class<?> cls:classList){
            ClassUtil.loadClass(cls.getName(),true);
        }
        
        //特别初始化
        if(servletContext != null){//因为表单请求可能带有文件上传，需要初始化Servlet相关设置
            FormRequestHelper.init(servletContext);
        }
        
        //装载的类日志分析
        LOGGER.debug("Filter implements\tx"+FilterHelper.getSortedFilterList().size());
        LOGGER.debug("@Controllers :\tx"+ClassHelper.getControllerClassSet().size());
        LOGGER.debug("@Service :\tx"+ClassHelper.getServiceClassSet().size());
        LOGGER.debug("@Dao :\tx"+ClassHelper.getClassSetByAnnotation(Dao.class).size());
        LOGGER.debug("@Table :\tx"+TableHelper.getEntityClassMap().size());
        LOGGER.debug("ACTION_LIST :\tx"+ControllerHelper.getActionList().size());
        LOGGER.debug("jw framework started success!");
    }
}
