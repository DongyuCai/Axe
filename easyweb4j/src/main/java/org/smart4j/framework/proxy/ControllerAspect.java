package org.smart4j.framework.proxy;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller 拦截
 * Created by CaiDongYu on 2016/4/14.
 */
//@Aspect(Controller.class)
public class ControllerAspect extends AspectProxy{

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);

    //TODO:如果单列，那么会有多线程问题
    private long begin;

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        LOGGER.debug("--------------begin---------------");
        LOGGER.debug(cls.getName()+"#"+method.getName());
        begin = System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
        LOGGER.debug("time:"+(System.currentTimeMillis()-begin));
        LOGGER.debug("---------------end----------------");
    }
}
