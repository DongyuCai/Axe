/**
 * MIT License
 * 
 * Copyright (c) 2017 The Axe Project
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
 * @author CaiDongyu on 2016/4/19.
 */
@Aspect(Service.class)
public class TransactionAspect extends AspectProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionAspect.class);

    private static final ThreadLocal<String> FLAG_HOLDER = new ThreadLocal<>();
    

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
    	//TODO:事务的传播机制
    	//目前比较简单，只是在哪儿打开，就在哪儿关闭，比如方法1通过托管获取并执行了其他Service方法，那么如果要打开事务，就只会打开一次
        String flag = FLAG_HOLDER.get();
        //开启事务，条件：1.没有打开过事务。2.方法上有@Tns注解
        if(flag == null && method.isAnnotationPresent(Tns.class)){
            DataBaseHelper.beginTransaction();
            FLAG_HOLDER.set(method.toGenericString());
            LOGGER.debug("begin transaction by TNS");
        }
    }
    
    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
    	String flag = FLAG_HOLDER.get();
    	//提交事务关闭连接，条件：1.开启过事务。2.在这个方法上开启了事务。3.方法上有@Tns注解
    	if(flag != null && flag.equals(method.toGenericString()) && method.isAnnotationPresent(Tns.class)){
        	DataBaseHelper.commitTransaction();
        	FLAG_HOLDER.remove();
        	LOGGER.debug("commit transaction by TNS");
        }
    }
    
    @Override
    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
    	String flag = FLAG_HOLDER.get();
    	if(flag != null && flag.equals(method.toGenericString()) && method.isAnnotationPresent(Tns.class)){
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
