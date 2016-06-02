package org.axe.proxy.implement;

import java.lang.reflect.Method;

import org.axe.annotation.aop.Aspect;
import org.axe.annotation.ioc.Service;
import org.axe.annotation.persistence.Tns;
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.proxy.base.AspectProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事务代理
 * 代理所有 @Service注解的类
 * 只增强 @Tns注解的方法
 * Created by CaiDongYu on 2016/4/19.
 */
@Aspect(Service.class)
public class TransactionAspect extends AspectProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionAspect.class);

    private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        boolean flag = FLAG_HOLDER.get();
        if(!flag && method.isAnnotationPresent(Tns.class)){
            FLAG_HOLDER.set(true);
            DataBaseHelper.beginTransaction();
            LOGGER.debug("begin transaction by TNS");
        }
    }
    
    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
    	boolean flag = FLAG_HOLDER.get();
    	if(flag && method.isAnnotationPresent(Tns.class)){
        	DataBaseHelper.commitTransaction();
        	LOGGER.debug("commit transaction by TNS");
        	FLAG_HOLDER.remove();
        }
    }
    
    @Override
    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
    	boolean flag = FLAG_HOLDER.get();
    	if(flag && method.isAnnotationPresent(Tns.class)){
    		 DataBaseHelper.rollbackTransaction();
             LOGGER.debug("rollback transaction",e);
             FLAG_HOLDER.remove();
    	}
    }
    
    /*@Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result;
        boolean flag = FLAG_HOLDER.get();
        Method method = proxyChain.getTargetMethod();
        if(!flag && method.isAnnotationPresent(Tns.class)){
            FLAG_HOLDER.set(true);
            try {
                DataBaseHelper.beginTransaction();
                LOGGER.debug("begin transaction by TNS");
                result = proxyChain.doProxyChain();
                DataBaseHelper.commitTransaction();
                LOGGER.debug("commit transaction by TNS");
            } catch (Exception e){
                DataBaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction",e);
                throw e;
            } finally {
                FLAG_HOLDER.remove();
            }
        } else {
            result = proxyChain.doProxyChain();
        }
        return result;
    }*/
}
