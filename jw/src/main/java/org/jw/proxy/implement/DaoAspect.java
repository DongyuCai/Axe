package org.jw.proxy.implement;

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

import org.jw.annotation.aop.Aspect;
import org.jw.annotation.persistence.Dao;
import org.jw.annotation.persistence.Sql;
import org.jw.bean.persistence.Page;
import org.jw.bean.persistence.PageConfig;
import org.jw.helper.persistence.DataBaseHelper;
import org.jw.helper.persistence.SqlHelper;
import org.jw.interface_.persistence.BaseRepository;
import org.jw.proxy.base.Proxy;
import org.jw.proxy.base.ProxyChain;
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
		Class<?>[] parameterTypes = targetMethod.getParameterTypes();
		Class<?> daoClass = proxyChain.getTargetClass();
		
		if(targetMethod.isAnnotationPresent(Sql.class)){
			Sql sqlAnnotation = targetMethod.getAnnotation(Sql.class);
			String sql = sqlAnnotation.value();
			
			//#解析Sql中的类名字段名
			sql = SqlHelper.convertHql2Sql(sql);
			
			String sqlUpperCase = sql.toUpperCase();
			if(sqlUpperCase.startsWith("SELECT") || sqlUpperCase.contains(" SELECT ")){
				Type returnType = targetMethod.getGenericReturnType();
				Class<?> rawType = targetMethod.getReturnType();
				if(returnType instanceof ParameterizedType){
					Type[] actualTypes = ((ParameterizedType) returnType).getActualTypeArguments();
					//带泛型的，只支持Page、List、Map这样
					if(Page.class.isAssignableFrom(rawType) || //如果要求返回类型是Page分页
						ReflectionUtil.compareType(List.class,rawType)){
						Type listParamType = actualTypes[0];
						
						if(listParamType instanceof ParameterizedType){
							//List<Map<String,Object>>
							if(ReflectionUtil.compareType(Map.class, (Class<?>)((ParameterizedType) listParamType).getRawType())){
								result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
							}
						}else if(listParamType instanceof WildcardType){
							//List<?>
							result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
						}else{
							if(ReflectionUtil.compareType(Object.class, (Class<?>)listParamType)){
								//List<Object>
								result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
							}else if(ReflectionUtil.compareType(Map.class, (Class<?>)listParamType)){
								//List<Map>
								result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
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
								result = getBasetypeOrDateList(sql, methodParams, parameterTypes);
							}else{
								//Entity
								result = DataBaseHelper.queryEntityList((Class<?>)listParamType, sql, methodParams, parameterTypes);
							}
						}
						
						if(Page.class.isAssignableFrom(rawType)){
							//如果是分页，包装返回结果
							result = pageResult(sql, methodParams, parameterTypes, (List<?>)result);
						}
					}else if(ReflectionUtil.compareType(Map.class, rawType)){
						//Map无所谓里面的泛型
						result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
					}
				}else{
					if(Page.class.isAssignableFrom(rawType) || 
							ReflectionUtil.compareType(List.class, rawType)){
						//Page、List
						result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
						

						if(Page.class.isAssignableFrom(rawType)){
							//如果是分页，包装返回结果
							result = pageResult(sql, methodParams, parameterTypes, (List<?>)result);
						}
					}else if(ReflectionUtil.compareType(Map.class, rawType)){
						//Map
						result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
					}else if(ReflectionUtil.compareType(Object.class, rawType)){
						//Object
						result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
					}else if(ReflectionUtil.compareType(String.class, rawType) || 
							ReflectionUtil.compareType(Date.class, rawType) || 
							ReflectionUtil.compareType(Byte.class, rawType) || 
							ReflectionUtil.compareType(Boolean.class, rawType) || 
							ReflectionUtil.compareType(Short.class, rawType) || 
							ReflectionUtil.compareType(Character.class, rawType) || 
							ReflectionUtil.compareType(Integer.class, rawType) || 
							ReflectionUtil.compareType(Long.class, rawType) || 
							ReflectionUtil.compareType(Float.class, rawType) || 
							ReflectionUtil.compareType(Double.class, rawType)){
						//String
						result = getBasetypeOrDate(sql, methodParams, parameterTypes);
					}else if((rawType).isPrimitive()){
						//基本类型
						result = DataBaseHelper.queryPrimitive(sql, methodParams, parameterTypes);
					}else{
						//Entity
						result = DataBaseHelper.queryEntity(rawType, sql, methodParams, parameterTypes);
					}
				}
			}else{
				result = DataBaseHelper.executeUpdate(sql, methodParams, parameterTypes);
			}
		}else if(BaseRepository.class.isAssignableFrom(daoClass) && !ReflectionUtil.compareType(BaseRepository.class,daoClass)){
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
				//# Repository.getEntity(T entity);
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
	
	private Object getBasetypeOrDate(String sql,Object[] methodParams,Class<?>[] parameterTypes){
		//Date
		Map<String,Object> resultMap = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
		Set<String> keySet = resultMap.keySet();
		return keySet.size() == 1 ? resultMap.get(keySet.iterator().next()) : null;
	}
	
	private Object getBasetypeOrDateList(String sql,Object[] methodParams,Class<?>[] parameterTypes){
		List<Map<String,Object>> resultList = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
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
	
	/**
	 * 包装List返回结果成分页
	 */
	private <T> Page<T> pageResult(String sql, Object[] params, Class<?>[] paramTypes, List<T> records){
		PageConfig pageConfig= SqlHelper.getPageConfigFromParams(params, paramTypes);
		long count = DataBaseHelper.countQuery(sql, params, paramTypes);
		pageConfig = pageConfig == null?new PageConfig(1,count):pageConfig;
		long pages = count/pageConfig.getPageSize();
		if(pages*pageConfig.getPageSize() < count)
			pages++;
		
		return new Page<>(records, pageConfig, count, pages);
	}
}
