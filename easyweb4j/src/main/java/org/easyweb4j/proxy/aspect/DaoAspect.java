package org.easyweb4j.proxy.aspect;

import java.lang.reflect.Method;

import org.easyweb4j.annotation.Aspect;
import org.easyweb4j.annotation.Dao;
import org.easyweb4j.annotation.Sql;
import org.easyweb4j.helper.DataBaseHelper;
import org.easyweb4j.proxy.Proxy;
import org.easyweb4j.proxy.ProxyChain;


/**
 * Dao代理
 * 代理所有 @Dao注解的接口
 * Created by CaiDongYu on 2016/4/19.
 */
@Aspect(Dao.class)
public class DaoAspect implements Proxy{

	@Override
	public Object doProxy(ProxyChain proxyChain) throws Throwable {
		Object result = null;
		Method targetMethod = proxyChain.getTargetMethod();
		Object[] methodParams = proxyChain.getMethodParams();
		if(targetMethod.isAnnotationPresent(Sql.class)){
			Sql sqlAnnotation = targetMethod.getAnnotation(Sql.class);
			String sql = sqlAnnotation.value();
			
			String sqlUpperCase = sql.toUpperCase();
			if(sqlUpperCase.contains(" SELECT ")){
				result = DataBaseHelper.queryEntity(targetMethod.getReturnType(), sql, methodParams);
			}
		}
		return result;
	}
}
