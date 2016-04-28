package org.easyweb4j;

import org.easyweb4j.helper.*;
import org.easyweb4j.util.ClassUtil;

import javax.servlet.ServletContext;

/**
 * 加载并初始化 Helper 类
 * Created by CaiDongYu on 2016/4/11.
 */
public final class HelperLoader {

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
                ControllerHelper.class
        };
        for (Class<?> cls:classList){
            ClassUtil.loadClass(cls.getName(),true);
        }

        if(servletContext != null){
            FormRequestHelper.init(servletContext);
        }
    }
}
