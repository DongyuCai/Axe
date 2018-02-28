/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.aop.Aspect;
import org.axe.annotation.persistence.Dao;
import org.axe.annotation.persistence.Sql;
import org.axe.bean.persistence.Page;
import org.axe.bean.persistence.PageConfig;
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.helper.persistence.SqlHelper;
import org.axe.interface_.persistence.BaseRepository;
import org.axe.interface_.proxy.Proxy;
import org.axe.proxy.base.ProxyChain;
import org.axe.util.CastUtil;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;


/**
 * Dao代理
 * 代理所有 @Dao注解的接口
 * @author CaiDongyu on 2016/4/19.
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
		String daoConfigDataSource = null;
		if(daoClass.isAnnotationPresent(Dao.class)){
			daoConfigDataSource = daoClass.getAnnotation(Dao.class).dataSource();
		}
		
		
		if(targetMethod.isAnnotationPresent(Sql.class)){
			Sql sqlAnnotation = targetMethod.getAnnotation(Sql.class);
			String sql = sqlAnnotation.value();
			
			//#解析指令代码
			sql = SqlHelper.convertSqlAppendCommand(sql, methodParams);
			//#解析Sql中的类名字段名
			String[] sqlAndDataSourceName = SqlHelper.convertHql2Sql(sql);
			sql = sqlAndDataSourceName[0];
			if(StringUtil.isEmpty(daoConfigDataSource)){
				daoConfigDataSource = sqlAndDataSourceName[1];
			}
			//#空格格式化，去掉首位空格，规范中间的空格{
			sql = sql.trim();
			while(sql.contains("  ")){
				sql = sql.replaceAll("  ", " ");
	    	}
			sql = sql.trim();
			//}
			
			String sqlUpperCase = sql.toUpperCase();
			if(sqlUpperCase.startsWith("SELECT")){
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
								if(StringUtil.isEmpty(daoConfigDataSource)){
									result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
								}else{
									result = DataBaseHelper.queryList(sql, methodParams, parameterTypes, daoConfigDataSource);
								}
							}
						}else if(listParamType instanceof WildcardType){
							//List<?>
							if(StringUtil.isEmpty(daoConfigDataSource)){
								result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
							}else{
								result = DataBaseHelper.queryList(sql, methodParams, parameterTypes, daoConfigDataSource);
							}
						}else{
							if(ReflectionUtil.compareType(Object.class, (Class<?>)listParamType)){
								//List<Object>
								if(StringUtil.isEmpty(daoConfigDataSource)){
									result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
								}else{
									result = DataBaseHelper.queryList(sql, methodParams, parameterTypes, daoConfigDataSource);
								}
							}else if(ReflectionUtil.compareType(Map.class, (Class<?>)listParamType)){
								//List<Map>
								if(StringUtil.isEmpty(daoConfigDataSource)){
									result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
								}else{
									result = DataBaseHelper.queryList(sql, methodParams, parameterTypes, daoConfigDataSource);
								}
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
								result = getBasetypeOrDateList(sql, methodParams, parameterTypes, daoConfigDataSource);
							}else{
								//Entity
								if(StringUtil.isEmpty(daoConfigDataSource)){
									result = DataBaseHelper.queryEntityList((Class<?>)listParamType, sql, methodParams, parameterTypes);
								}else{
									result = DataBaseHelper.queryEntityList((Class<?>)listParamType, sql, methodParams, parameterTypes, daoConfigDataSource);
								}
							}
						}
						
						if(Page.class.isAssignableFrom(rawType)){
							//如果是分页，包装返回结果
							result = pageResult(sql, methodParams, parameterTypes, (List<?>)result, daoConfigDataSource);
						}
					}else if(ReflectionUtil.compareType(Map.class, rawType)){
						//Map无所谓里面的泛型
						if(StringUtil.isEmpty(daoConfigDataSource)){
							result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
						}else{
							result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes, daoConfigDataSource);
						}
					}
				}else{
					if(Page.class.isAssignableFrom(rawType) || 
							ReflectionUtil.compareType(List.class, rawType)){
						//Page、List
						if(StringUtil.isEmpty(daoConfigDataSource)){
							result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
						}else{
							result = DataBaseHelper.queryList(sql, methodParams, parameterTypes, daoConfigDataSource);
						}
						

						if(Page.class.isAssignableFrom(rawType)){
							//如果是分页，包装返回结果
							result = pageResult(sql, methodParams, parameterTypes, (List<?>)result, daoConfigDataSource);
						}
					}else if(ReflectionUtil.compareType(Map.class, rawType)){
						//Map
						if(StringUtil.isEmpty(daoConfigDataSource)){
							result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
						}else{
							result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes, daoConfigDataSource);
						}
					}else if(ReflectionUtil.compareType(Object.class, rawType)){
						//Object
						if(StringUtil.isEmpty(daoConfigDataSource)){
							result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
						}else{
							result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes, daoConfigDataSource);
						}
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
						result = getBasetypeOrDate(sql, methodParams, parameterTypes, daoConfigDataSource);
						result = CastUtil.castType(result, rawType);
					}else if((rawType).isPrimitive()){
						//基本类型
						if(StringUtil.isEmpty(daoConfigDataSource)){
							result = DataBaseHelper.queryPrimitive(sql, methodParams, parameterTypes);
						}else{
							result = DataBaseHelper.queryPrimitive(sql, methodParams, parameterTypes, daoConfigDataSource);
						}
					}else{
						//Entity
						if(StringUtil.isEmpty(daoConfigDataSource)){
							result = DataBaseHelper.queryEntity(rawType, sql, methodParams, parameterTypes);
						}else{
							result = DataBaseHelper.queryEntity(rawType, sql, methodParams, parameterTypes, daoConfigDataSource);
						}
					}
				}
			}else{
				if(StringUtil.isEmpty(daoConfigDataSource)){
					result = DataBaseHelper.executeUpdate(sql, methodParams, parameterTypes);
				}else{
					result = DataBaseHelper.executeUpdate(sql, methodParams, parameterTypes, daoConfigDataSource);
				}
			}
		}else if(BaseRepository.class.isAssignableFrom(daoClass) && !ReflectionUtil.compareType(BaseRepository.class,daoClass)){
			String methodName = targetMethod.getName();
			Class<?>[] paramAry = targetMethod.getParameterTypes();
			if("insertEntity".equals(methodName)){
				//# Repository.insertEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0],Object.class)){
					Object entity = methodParams[0];
					if(StringUtil.isEmpty(daoConfigDataSource)){
						result = DataBaseHelper.insertEntity(entity);
					}else{
						result = DataBaseHelper.insertEntity(entity,daoConfigDataSource);
					}
				}
			}else if("deleteEntity".equals(methodName)){
				//# Repository.deleteEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0],Object.class)){
					Object entity = methodParams[0];
					if(StringUtil.isEmpty(daoConfigDataSource)){
						result = DataBaseHelper.deleteEntity(entity);
					}else{
						result = DataBaseHelper.deleteEntity(entity,daoConfigDataSource);
					}
				}
			}else if("updateEntity".equals(methodName)){
				//# Repository.updateEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0],Object.class)){
					Object entity = methodParams[0];
					if(StringUtil.isEmpty(daoConfigDataSource)){
						result = DataBaseHelper.updateEntity(entity);
					}else{
						result = DataBaseHelper.updateEntity(entity,daoConfigDataSource);
					}
				}
			}else if("getEntity".equals(methodName)){
				//# Repository.getEntity(T entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0],Object.class)){
					Object entity = methodParams[0];
					if(StringUtil.isEmpty(daoConfigDataSource)){
						result = DataBaseHelper.getEntity(entity);
					}else{
						result = DataBaseHelper.getEntity(entity,daoConfigDataSource);
					}
				}
			}else if("saveEntity".equals(methodName)){
				//# Repository.saveEntity(Object entity);
				if(paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0],Object.class)){
					Object entity = methodParams[0];
					if(StringUtil.isEmpty(daoConfigDataSource)){
						result = DataBaseHelper.insertOnDuplicateKeyEntity(entity);
					}else{
						result = DataBaseHelper.insertOnDuplicateKeyEntity(entity,daoConfigDataSource);
					}
				}
			}else{
				result = proxyChain.doProxyChain();
			}
		}else{
			result = proxyChain.doProxyChain();
		}
		return result;
	}
	
	private Object getBasetypeOrDate(String sql,Object[] methodParams,Class<?>[] parameterTypes,String daoConfigDataSource) throws SQLException{
		//Date
		Map<String,Object> resultMap = DataBaseHelper.queryMap(sql, methodParams, parameterTypes,daoConfigDataSource);
		do{
			if(resultMap == null) break;
			if(CollectionUtil.isEmpty(resultMap)) break;
			
			Set<String> keySet = resultMap.keySet();
			return resultMap.get(keySet.iterator().next());
		}while(false);
		return null;
	}
	
	private Object getBasetypeOrDateList(String sql,Object[] methodParams,Class<?>[] parameterTypes,String daoConfigDataSource) throws SQLException{
		List<Map<String,Object>> resultList = null;
		if(StringUtil.isEmpty(daoConfigDataSource)){
			resultList = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
		}else{
			resultList = DataBaseHelper.queryList(sql, methodParams, parameterTypes,daoConfigDataSource);
		}
		if(resultList != null && resultList.size() > 0){
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
	private <T> Page<T> pageResult(String sql, Object[] params, Class<?>[] paramTypes, List<T> records, String daoConfigDataSource){
		PageConfig pageConfig= SqlHelper.getPageConfigFromParams(params, paramTypes);
		Object[] params_ = new Object[params.length-1];
		for(int i=0;i<params_.length;i++){
			params_[i] = params[i];
		}
		Class<?>[] paramTypes_ = new Class<?>[paramTypes.length-1];
		for(int i=0;i<paramTypes_.length;i++){
			paramTypes_[i] = paramTypes[i];
		}
		long count = 0;
		if(StringUtil.isEmpty(daoConfigDataSource)){
			count = DataBaseHelper.countQuery(sql, params_, paramTypes_);
		}else{
			count = DataBaseHelper.countQuery(sql, params_, paramTypes_, daoConfigDataSource);
		}
		pageConfig = pageConfig == null?new PageConfig(1,count):pageConfig;
		long pages = count/pageConfig.getPageSize();
		if(pages*pageConfig.getPageSize() < count)
			pages++;
		
		return new Page<>(records, pageConfig, count, pages);
	}
}
