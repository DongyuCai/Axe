package org.jw.proxy.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jw.annotation.Aspect;
import org.jw.annotation.Dao;
import org.jw.annotation.Sql;
import org.jw.helper.DataBaseHelper;
import org.jw.helper.SqlHelper;
import org.jw.interface_.Repository;
import org.jw.proxy.Proxy;
import org.jw.proxy.ProxyChain;
import org.jw.util.ReflectionUtil;


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
		Class<?> daoClass = proxyChain.getTargetClass();
		
		if(targetMethod.isAnnotationPresent(Sql.class)){
			Sql sqlAnnotation = targetMethod.getAnnotation(Sql.class);
			String sql = sqlAnnotation.value();
			
			//#解析Sql中的类名字段名
			sql = SqlHelper.convertHql2Sql(sql);
			
			String sqlUpperCase = sql.toUpperCase();
			if(sqlUpperCase.startsWith("SELECT") || sqlUpperCase.contains(" SELECT ")){
				Type returnType = targetMethod.getGenericReturnType();
				if(returnType instanceof ParameterizedType){
					Type[] actualTypes = ((ParameterizedType) returnType).getActualTypeArguments();
					//带泛型的，只支持List、Map这样
					if(ReflectionUtil.compareType(List.class, (Class<?>)((ParameterizedType) returnType).getRawType())){
						Type listParamType = actualTypes[0];
						
						if(listParamType instanceof ParameterizedType){
							//List<Map<String,Object>>
							if(ReflectionUtil.compareType(Map.class, (Class<?>)((ParameterizedType) listParamType).getRawType())){
								result = DataBaseHelper.queryList(sql, methodParams);
							}
						}else if(listParamType instanceof WildcardType){
							//List<?>
							result = DataBaseHelper.queryList(sql, methodParams);
						}else{
							if(ReflectionUtil.compareType(Object.class, (Class<?>)listParamType)){
								//List<Object>
								result = DataBaseHelper.queryList(sql, methodParams);
							}else if(ReflectionUtil.compareType(Map.class, (Class<?>)listParamType)){
								//List<Map>
								result = DataBaseHelper.queryList(sql, methodParams);
							}else if(ReflectionUtil.compareType(String.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Date.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Byte.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Boolean.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Short.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Character.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Integer.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Long.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Float.class, (Class<?>)listParamType) || 
									ReflectionUtil.compareType(Double.class, (Class<?>)listParamType)){
								//List<String>
								result = getBasetypeOrDateList(sql, methodParams);
							}else{
								//Entity
								result = DataBaseHelper.queryEntityList((Class<?>)listParamType, sql, methodParams);
							}
						}
					}else if(ReflectionUtil.compareType(Map.class, (Class<?>)((ParameterizedType) returnType).getRawType())){
						//Map无所谓里面的泛型
						result = DataBaseHelper.queryMap(sql, methodParams);
					}
				}else{
					if(ReflectionUtil.compareType(List.class, (Class<?>)returnType)){
						//List
						result = DataBaseHelper.queryList(sql, methodParams);
					}else if(ReflectionUtil.compareType(Map.class, (Class<?>)returnType)){
						//Map
						result = DataBaseHelper.queryMap(sql, methodParams);
					}else if(ReflectionUtil.compareType(Object.class, (Class<?>)returnType)){
						//Object
						result = DataBaseHelper.queryMap(sql, methodParams);
					}else if(ReflectionUtil.compareType(String.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Date.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Byte.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Boolean.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Short.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Character.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Integer.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Long.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Float.class, (Class<?>)returnType) || 
							ReflectionUtil.compareType(Double.class, (Class<?>)returnType)){
						//String
						result = getBasetypeOrDate(sql, methodParams);
					}else if(((Class<?>)returnType).isPrimitive()){
						//基本类型
						result = DataBaseHelper.queryPrimitive(sql, methodParams);
					}else{
						//Entity
						result = DataBaseHelper.queryEntity((Class<?>)returnType, sql, methodParams);
					}
				}
			}else{
				result = DataBaseHelper.executeUpdate(sql, methodParams);
			}
		}else if(Repository.class.isAssignableFrom(daoClass) && !ReflectionUtil.compareType(Repository.class,daoClass)){
			String methodName = targetMethod.getName();
			Parameter[] paramAry = targetMethod.getParameters();
			if("insertEntity".equals(methodName)){
				//# Repository.insertEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0].getType(),Object.class)){
					Object entity = methodParams[0];
					result = DataBaseHelper.insertEntity(entity);
				}
			}else if("deleteEntity".equals(methodName)){
				//# Repository.deleteEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0].getType(),Object.class)){
					Object entity = methodParams[0];
					result = DataBaseHelper.deleteEntity(entity);
				}
			}else if("updateEntity".equals(methodName)){
				//# Repository.updateEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0].getType(),Object.class)){
					Object entity = methodParams[0];
					result = DataBaseHelper.updateEntity(entity);
				}
			}else if("getEntity".equals(methodName)){
				//# Repository.getEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0].getType(),Object.class)){
					Object entity = methodParams[0];
					result = DataBaseHelper.getEntity(entity);
				}
			}else if("saveEntity".equals(methodName)){
				//# Repository.saveEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0].getType(),Object.class)){
					Object entity = methodParams[0];
					result = DataBaseHelper.insertOnDuplicateKeyEntity(entity);
				}
			}else{
				result = proxyChain.doProxyChain();
			}
		}else{
			result = proxyChain.doProxyChain();
		}
		return result;
	}
	
	private Object getBasetypeOrDate(String sql,Object[] methodParams){
		//Date
		Map<String,Object> resultMap = DataBaseHelper.queryMap(sql, methodParams);
		Set<String> keySet = resultMap.keySet();
		return keySet.size() == 1 ? resultMap.get(keySet.iterator().next()) : null;
	}
	
	private Object getBasetypeOrDateList(String sql,Object[] methodParams){
		List<Map<String,Object>> resultList = DataBaseHelper.queryList(sql, methodParams);
		if(resultList.size() > 0){
			List<Object> list = new ArrayList<Object>();
			for(Map<String,Object> row:resultList){
				if(row.size() == 1){
					list.add(row.entrySet().iterator().next().getValue());
				}
			}
			return list.size() > 0 ? list:null;
		}
		return null;
	}
	
}
