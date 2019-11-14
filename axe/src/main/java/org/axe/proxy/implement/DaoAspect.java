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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.aop.Aspect;
import org.axe.annotation.persistence.Dao;
import org.axe.annotation.persistence.ResultProxy;
import org.axe.annotation.persistence.Sql;
import org.axe.bean.persistence.Page;
import org.axe.bean.persistence.PageConfig;
import org.axe.bean.persistence.ShardingTableCreateTask;
import org.axe.bean.persistence.SqlPackage;
import org.axe.bean.persistence.TableSchema;
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.helper.persistence.DataSourceHelper;
import org.axe.helper.persistence.TableHelper;
import org.axe.interface_.persistence.BaseRepository;
import org.axe.interface_.persistence.Sharding;
import org.axe.interface_.persistence.SqlResultProxy;
import org.axe.interface_.proxy.Proxy;
import org.axe.proxy.base.ProxyChain;
import org.axe.util.CastUtil;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;
import org.axe.util.sql.CommonSqlUtil;
import org.axe.util.sql.MySqlUtil;
//import org.axe.util.sql.MySqlUtil;
import org.axe.util.sql.OracleUtil;

/**
 * Dao代理 代理所有 @Dao注解的接口
 * 
 * @author CaiDongyu on 2016/4/19.
 */
@Aspect(Dao.class)
public class DaoAspect implements Proxy {

	@Override
	public Object doProxy(ProxyChain proxyChain) throws Throwable {
		Object result = null;
		Method targetMethod = proxyChain.getTargetMethod();
		Object[] methodParams = proxyChain.getMethodParams();
		Class<?>[] parameterTypes = targetMethod.getParameterTypes();
		Class<?> daoClass = proxyChain.getTargetClass();
		// 先以Dao上的数据源为准，后面如果Sql与Entity的数据源设置不一样，提示错误
		String daoDataSourceName = daoClass.getAnnotation(Dao.class).dataSource();
		if(StringUtil.isEmpty(daoDataSourceName)){
			daoDataSourceName = DataSourceHelper.getDefaultDataSourceName();
		}

		// 如果有sql结果代理器
		Type returnType = null;
		Class<?> rawType = null;
		SqlResultProxy sqlResultProxy = null;
		if (targetMethod.isAnnotationPresent(ResultProxy.class)) {
			ResultProxy resultProxy = targetMethod.getAnnotation(ResultProxy.class);
			Class<? extends SqlResultProxy> proxyClass = resultProxy.value();
			returnType = resultProxy.returnType();// 伪造返回结果类型，这样查询就都是List<Map<String,Object>>结果集
			rawType = resultProxy.rawType();// 伪造返回结果类型，这样查询就都是List<Map<String,Object>>结果集
			sqlResultProxy = ReflectionUtil.newInstance(proxyClass);
		}

		if (targetMethod.isAnnotationPresent(Sql.class)) {
			Sql sqlAnnotation = targetMethod.getAnnotation(Sql.class);
			String rawSql = sqlAnnotation.value();
			String headAfterUnion = sqlAnnotation.headAfterUnion();//2019/2/14 sql头部补充
			String tailAfterUnion = sqlAnnotation.tailAfterUnion();//2019/2/14 sql尾句补充

			// #解析指令代码
			rawSql = CommonSqlUtil.convertSqlAppendCommand(rawSql, methodParams);
			headAfterUnion = CommonSqlUtil.convertSqlAppendCommand(headAfterUnion, methodParams);
			tailAfterUnion = CommonSqlUtil.convertSqlAppendCommand(tailAfterUnion, methodParams);
			
			// #解析Sql中的类名字段名
			// #根据sql匹配出Entity类
			// CaiDongyu 2019/2/13{ 对分片操作进行处理，一表操作转为多表操作
			Map<String, TableSchema> sqlEntityTableMap = CommonSqlUtil.convertSqlEntity2Table(rawSql);
			
			List<Map<String,String>> sqlEntityTableNameList = new ArrayList<>();
			for(String entityClassSimpleName:sqlEntityTableMap.keySet()){
				TableSchema tableSchema = sqlEntityTableMap.get(entityClassSimpleName);
				Map<String,String> sqlEntityTableNameMap = new HashMap<>();
				//分片的需要获取分片表集合
				if(tableSchema.getSharding()){
					StringBuilder sqlBuffer = new StringBuilder();
					sqlBuffer.append("SELECT sharding_flag FROM ")
					.append(tableSchema.getTableName())
					.append("_sharding_gt ORDER BY sharding_flag ASC");
					List<Map<String, Object>> queryList = DataBaseHelper.queryList(sqlBuffer.toString(), new Object[]{}, new Class<?>[]{},daoDataSourceName);
					for(Map<String,Object> row:queryList){
						sqlBuffer.setLength(0);
						sqlBuffer.append(tableSchema.getTableName()).append("_sharding_").append(row.get("sharding_flag").toString());
						sqlEntityTableNameMap.put(sqlBuffer.toString(), entityClassSimpleName);
					}
				}else{
					sqlEntityTableNameMap.put(tableSchema.getTableName(), entityClassSimpleName);
				}
				sqlEntityTableNameList.add(sqlEntityTableNameMap);
			}
			
			List<String> sqlList = CommonSqlUtil.convertRawSql(rawSql,sqlEntityTableMap,sqlEntityTableNameList);
			
			if(CollectionUtil.isNotEmpty(sqlList)){
				List<String> headAfterUnionList = CommonSqlUtil.convertRawSql(headAfterUnion,sqlEntityTableMap,sqlEntityTableNameList);
				List<String> tailAfterUnionList = CommonSqlUtil.convertRawSql(tailAfterUnion,sqlEntityTableMap,sqlEntityTableNameList);
				if(CollectionUtil.isNotEmpty(headAfterUnionList)){
					headAfterUnion = headAfterUnionList.get(0);//取第一个，所以不能head里出现表名并且是分表的实体类，这样只会被第一个分表表名有效
				}
				if(CollectionUtil.isNotEmpty(tailAfterUnionList)){
					tailAfterUnion = tailAfterUnionList.get(0);
				}
				
				//sqlAry里的sql语句，都是同一性质的操作
				if (sqlList.get(0).trim().toUpperCase().startsWith("SELECT")) {
				//}	
					returnType = returnType == null ? targetMethod.getGenericReturnType() : returnType;
					rawType = rawType == null ? targetMethod.getReturnType() : rawType;
					if (returnType instanceof ParameterizedType) {
						Type[] actualTypes = ((ParameterizedType) returnType).getActualTypeArguments();
						// 带泛型的，只支持Page、List、Map这样
						if (Page.class.isAssignableFrom(rawType) || // 如果要求返回类型是Page分页
								ReflectionUtil.compareType(List.class, rawType)) {
							
							convertPageSqlList(sqlList, daoDataSourceName, methodParams, parameterTypes);
							
							String sql = unionSqlAry(sqlList,headAfterUnion,tailAfterUnion);
							result = listResult(actualTypes[0], daoDataSourceName, sql, methodParams, parameterTypes);

							if (Page.class.isAssignableFrom(rawType)) {
								// 如果是分页，包装返回结果
								result = pageResult(sql, methodParams, parameterTypes, (List<?>) result, daoDataSourceName);
							}
						} else if (ReflectionUtil.compareType(Map.class, rawType)) {
							// Map无所谓里面的泛型
							/*if (StringUtil.isEmpty(dataSourceName)) {
								result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
							} else {*/
								result = DataBaseHelper.queryMap(unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes, daoDataSourceName);
//							}
						}
					} else {
						if (Page.class.isAssignableFrom(rawType) || ReflectionUtil.compareType(List.class, rawType)) {
							// Page、List
							/*if (StringUtil.isEmpty(dataSourceName)) {
								result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
							} else {
								result = DataBaseHelper.queryList(sql, methodParams, parameterTypes, dataSourceName);
							}*/
							convertPageSqlList(sqlList, daoDataSourceName, methodParams, parameterTypes);
							
							String sql = unionSqlAry(sqlList,headAfterUnion,tailAfterUnion);
							result = listResult(returnType, daoDataSourceName, sql, methodParams, parameterTypes);

							if (Page.class.isAssignableFrom(rawType)) {
								// 如果是分页，包装返回结果
								result = pageResult(sql, methodParams, parameterTypes, (List<?>) result, daoDataSourceName);
							}
						} else if (ReflectionUtil.compareType(Map.class, rawType)) {
							// Map
							/*if (StringUtil.isEmpty(dataSourceName)) {
								result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
							} else {*/
								result = DataBaseHelper.queryMap(unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes, daoDataSourceName);
//							}
						} else if (ReflectionUtil.compareType(Object.class, rawType)) {
							// Object
							/*if (StringUtil.isEmpty(dataSourceName)) {
								result = DataBaseHelper.queryMap(sql, methodParams, parameterTypes);
							} else {*/
								result = DataBaseHelper.queryMap(unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes, daoDataSourceName);
//							}
						} else if (ReflectionUtil.compareType(String.class, rawType)
								|| ReflectionUtil.compareType(Byte.class, rawType)
								|| ReflectionUtil.compareType(Boolean.class, rawType)
								|| ReflectionUtil.compareType(Short.class, rawType)
								|| ReflectionUtil.compareType(Character.class, rawType)
								|| ReflectionUtil.compareType(Integer.class, rawType)
								|| ReflectionUtil.compareType(Long.class, rawType)
								|| ReflectionUtil.compareType(Float.class, rawType)
								|| ReflectionUtil.compareType(Double.class, rawType)) {
							// String
							result = getBasetypeOrDate(unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes, daoDataSourceName);
							result = CastUtil.castType(result, rawType);
						} else if ((rawType).isPrimitive()) {
							if (ReflectionUtil.compareType(void.class, rawType)) {
								// void
								/*if (StringUtil.isEmpty(dataSourceName)) {
									result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
								} else {*/
									result = DataBaseHelper.queryList(unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes, daoDataSourceName);
//								}
							} else {
								// 基本类型
								/*if (StringUtil.isEmpty(dataSourceName)) {
									result = DataBaseHelper.queryPrimitive(sql, methodParams, parameterTypes);
								} else {*/
									result = DataBaseHelper.queryPrimitive(unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes, daoDataSourceName);
//								}
							}
						} else {
							// Entity
							if (StringUtil.isEmpty(daoDataSourceName)) {
								result = DataBaseHelper.queryEntity(rawType, unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes);
							} else {
								result = DataBaseHelper.queryEntity(rawType, unionSqlAry(sqlList,headAfterUnion,tailAfterUnion), methodParams, parameterTypes, daoDataSourceName);
							}
						}
					}
				} else {
					String[] sqlAry = new String[sqlList.size()];
					for(int i=0;i<sqlAry.length;i++){
						sqlAry[i] = sqlList.get(i);
					}
					if (StringUtil.isEmpty(daoDataSourceName)) {
						result = DataBaseHelper.executeUpdate(sqlAry, methodParams, parameterTypes);
					} else {
						result = DataBaseHelper.executeUpdate(sqlAry, methodParams, parameterTypes, daoDataSourceName);
					}
				}
			}
		} else if (BaseRepository.class.isAssignableFrom(daoClass)
				&& !ReflectionUtil.compareType(BaseRepository.class, daoClass)) {
			String methodName = targetMethod.getName();
			Class<?>[] paramAry = targetMethod.getParameterTypes();
			if ("insertEntity".equals(methodName)) {
				// # Repository.insertEntity(Object entity);
				if (paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0], Object.class)) {
					Object entity = methodParams[0];
					//2018/12/29 插入前分片检测
					insertDataShardingTableCheck(entity, daoDataSourceName);
					
					if (StringUtil.isEmpty(daoDataSourceName)) {
						result = DataBaseHelper.insertEntity(entity);
					} else {
						result = DataBaseHelper.insertEntity(entity, daoDataSourceName);
					}

					//2018/12/29 插入后的分片数据表状态更新
					shardingTableStatusCheck(entity, daoDataSourceName);
				}
			} else if ("deleteEntity".equals(methodName)) {
				// # Repository.deleteEntity(Object entity);
				if (paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0], Object.class)) {
					Object entity = methodParams[0];
					if (StringUtil.isEmpty(daoDataSourceName)) {
						result = DataBaseHelper.deleteEntity(entity);
					} else {
						result = DataBaseHelper.deleteEntity(entity, daoDataSourceName);
					}
					//2018/12/29 插入后的分片数据表状态更新
					shardingTableStatusCheck(entity, daoDataSourceName);
				}
			} else if ("updateEntity".equals(methodName)) {
				// # Repository.updateEntity(Object entity);
				if (paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0], Object.class)) {
					Object entity = methodParams[0];
					if (StringUtil.isEmpty(daoDataSourceName)) {
						result = DataBaseHelper.updateEntity(entity);
					} else {
						result = DataBaseHelper.updateEntity(entity, daoDataSourceName);
					}
				}
			} else if ("getEntity".equals(methodName)) {
				// # Repository.getEntity(T entity);
				if (paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0], Object.class)) {
					Object entity = methodParams[0];
					if (StringUtil.isEmpty(daoDataSourceName)) {
						result = DataBaseHelper.getEntity(entity);
					} else {
						result = DataBaseHelper.getEntity(entity, daoDataSourceName);
					}
				}
			} else if ("saveEntity".equals(methodName)) {
				// # Repository.saveEntity(Object entity);
				if (paramAry.length == 1 && ReflectionUtil.compareType(paramAry[0], Object.class)) {
					Object entity = methodParams[0];
					//2018/12/29 插入前分片检测
					insertDataShardingTableCheck(entity, daoDataSourceName);
					
					if (StringUtil.isEmpty(daoDataSourceName)) {
						result = DataBaseHelper.insertOnDuplicateKeyEntity(entity);
					} else {
						result = DataBaseHelper.insertOnDuplicateKeyEntity(entity, daoDataSourceName);
					}
					
					//2018/12/29 插入后的分片数据表状态更新
					shardingTableStatusCheck(entity, daoDataSourceName);
				}
			} else {
				result = proxyChain.doProxyChain();
			}
		} else {
			result = proxyChain.doProxyChain();
		}

		// 如果有Sql结果代理器，那么代理一下
		if (sqlResultProxy != null) {
			result = sqlResultProxy.proxy(result);
		}
		return result;
	}
	
	//对sqlList中的每句Sql都预先做分页处理，进入DataBaseHelper后，会对总句进行分页处理
	private void convertPageSqlList(List<String> sqlList,String dataSourceName,Object[] methodParams, Class<?>[] parameterTypes){
		if(CollectionUtil.isNotEmpty(sqlList) && sqlList.size() > 1){
			//如果就1个sql，那么是不需要这样的，因为后面不会有union合并
			if(DataSourceHelper.isMySql(dataSourceName)){
	    		for(int i=0;i<sqlList.size();i++){
	    			SqlPackage sp = MySqlUtil.convertPagConfig(sqlList.get(i), methodParams, parameterTypes,true);
					sqlList.set(i, sp.getSql());
				}
	    	}else if(DataSourceHelper.isOracle(dataSourceName)){
	    		for(int i=0;i<sqlList.size();i++){
	    			SqlPackage sp = OracleUtil.convertPagConfig(sqlList.get(i), methodParams, parameterTypes);
					sqlList.set(i, sp.getSql());
				}
	    	}
		}
	}
	
	private String unionSqlAry(List<String> sqlList,String headAfterUnion,String tailAfterUnion){
		//Select 需要聚合union all所有结果
		StringBuilder sqlBuf = new StringBuilder();
		if(StringUtil.isNotEmpty(headAfterUnion)){
			sqlBuf.append(headAfterUnion).append(" ");
		}
		if(sqlList.size() > 1){
			sqlBuf.append("SELECT * FROM (");
			for(int i=0;i<sqlList.size();i++){
				String tmpSql = sqlList.get(i);
				if(i > 0){
					sqlBuf.append("UNION ALL");
				}
				sqlBuf.append("(").append(tmpSql).append(")");
			}
			sqlBuf.append(") t_").append(StringUtil.getRandomString(6));
		}else{
			//如果出现headAfterUnion或者tailAfterUnion，都要把中间sql包起来
			if(StringUtil.isNotEmpty(headAfterUnion) || StringUtil.isNotEmpty(tailAfterUnion)){
				sqlBuf.append("SELECT * FROM (");
				sqlBuf.append(sqlList.get(0));
				sqlBuf.append(") t_").append(StringUtil.getRandomString(6));
			}else{
				sqlBuf.append(sqlList.get(0));
			}
		}
		if(StringUtil.isNotEmpty(tailAfterUnion)){
			sqlBuf.append(" ").append(tailAfterUnion);
		}
		return sqlBuf.toString();//select情况下的最终sql，多条会合并成1条
	}

	private Object getBasetypeOrDate(String sql, Object[] methodParams, Class<?>[] parameterTypes,
			String dataSourceName) throws SQLException {
		// Date
		Map<String, Object> resultMap = DataBaseHelper.queryMap(sql, methodParams, parameterTypes, dataSourceName);
		do {
			if (resultMap == null)
				break;
			if (CollectionUtil.isEmpty(resultMap))
				break;

			Set<String> keySet = resultMap.keySet();
			return resultMap.get(keySet.iterator().next());
		} while (false);
		return null;
	}

	private Object getBasetypeOrDateList(String sql, Object[] methodParams, Class<?>[] parameterTypes,
			String dataSourceName) throws SQLException {
		List<Map<String, Object>> resultList = null;
		if (StringUtil.isEmpty(dataSourceName)) {
			resultList = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
		} else {
			resultList = DataBaseHelper.queryList(sql, methodParams, parameterTypes, dataSourceName);
		}
		List<Object> list = new ArrayList<Object>();
		if (CollectionUtil.isNotEmpty(resultList)) {
			for (Map<String, Object> row : resultList) {
				if (row.size() == 1) {
					list.add(row.entrySet().iterator().next().getValue());
				}
			}
		}
		return list;
	}
	

	private Object listResult(Type returnType, String dataSourceName, String sql, Object[] methodParams, Class<?>[] parameterTypes) throws SQLException{
		Object result = null;
		if (returnType instanceof ParameterizedType) {
			// List<Map<String,Object>>
			if (ReflectionUtil.compareType(Map.class,
					(Class<?>) ((ParameterizedType) returnType).getRawType())) {
				if (StringUtil.isEmpty(dataSourceName)) {
					result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
				} else {
					result = DataBaseHelper.queryList(sql, methodParams, parameterTypes,
							dataSourceName);
				}
			}
		} else if (returnType instanceof WildcardType) {
			// List<?>
			if (StringUtil.isEmpty(dataSourceName)) {
				result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
			} else {
				result = DataBaseHelper.queryList(sql, methodParams, parameterTypes,
						dataSourceName);
			}
		} else {
			if (ReflectionUtil.compareType(Object.class, (Class<?>) returnType)) {
				// List<Object>
				if (StringUtil.isEmpty(dataSourceName)) {
					result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
				} else {
					result = DataBaseHelper.queryList(sql, methodParams, parameterTypes,
							dataSourceName);
				}
			} else if (ReflectionUtil.compareType(Map.class, (Class<?>) returnType)) {
				// List<Map>
				if (StringUtil.isEmpty(dataSourceName)) {
					result = DataBaseHelper.queryList(sql, methodParams, parameterTypes);
				} else {
					result = DataBaseHelper.queryList(sql, methodParams, parameterTypes,
							dataSourceName);
				}
			} else if (ReflectionUtil.compareType(String.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Date.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Byte.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Boolean.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Short.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Character.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Integer.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Long.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Float.class, (Class<?>) returnType)
					|| ReflectionUtil.compareType(Double.class, (Class<?>) returnType)) {
				// List<String>
				result = getBasetypeOrDateList(sql, methodParams, parameterTypes, dataSourceName);
			} else {
				// Entity
				if (StringUtil.isEmpty(dataSourceName)) {
					result = DataBaseHelper.queryEntityList((Class<?>) returnType, sql, methodParams,
							parameterTypes);
				} else {
					result = DataBaseHelper.queryEntityList((Class<?>) returnType, sql, methodParams,
							parameterTypes, dataSourceName);
				}
			}
		}
		
		return result;
	}

	/**
	 * 包装List返回结果成分页
	 */
	private <T> Page<T> pageResult(String sql, Object[] params, Class<?>[] paramTypes, List<T> records,
			String dataSourceName) {
		
		PageConfig pageConfig = CommonSqlUtil.getPageConfigFromParams(params, paramTypes);
		Object[] params_ = new Object[params.length - 1];
		for (int i = 0; i < params_.length; i++) {
			params_[i] = params[i];
		}
		Class<?>[] paramTypes_ = new Class<?>[paramTypes.length - 1];
		for (int i = 0; i < paramTypes_.length; i++) {
			paramTypes_[i] = paramTypes[i];
		}
		long count = 0;
		if (StringUtil.isEmpty(dataSourceName)) {
			count = DataBaseHelper.countQuery(sql, params_, paramTypes_);
		} else {
			count = DataBaseHelper.countQuery(sql, params_, paramTypes_, dataSourceName);
		}
		pageConfig = pageConfig == null ? new PageConfig(1, count) : pageConfig;
		long pages = count / pageConfig.getPageSize();
		if (pages * pageConfig.getPageSize() < count)
			pages++;

		return new Page<>(records, pageConfig, count, pages);
	}
	
	/**
	 * 2018/12/29
	 * 增加Sharding分片机制，在做插入前，做表检测和创建
	 * 插入检测，只在insert和save两个操作下需要
	 */
	private void insertDataShardingTableCheck(Object entity,String dataSourceName) throws Exception{
		TableSchema tableSchema = TableHelper.getCachedTableSchema(entity);
		
		//只有需要分片的，才处理
		if(tableSchema.getSharding()){
			if(StringUtil.isEmpty(dataSourceName)){
				dataSourceName = tableSchema.getDataSourceName();
			}
			
			//*注意，这里的查询，都是排序后取的第一条，但是没用limit或者top，因为都是方言，所以只需要排序，DataBaseHelper.queryPrimitive默认会返回第一条
			Sharding sentity = (Sharding)entity;
			//如果是新增，数据分片id还是空的，就需要计算分片id，新增数据表等等的检测
			//如果是保存，那就不需要了。
			if(sentity.getShardingFlag() == null){
				//查询当前可以插入的表分片的id
				StringBuilder sqlBuffer = new StringBuilder();
				sqlBuffer.append("SELECT sharding_flag FROM ")
				.append(tableSchema.getTableName())
				.append("_sharding_gt WHERE sharding_table_status=1 ORDER BY sharding_flag ASC");
				Integer shardingFlag = null;
				shardingFlag = DataBaseHelper.queryPrimitive(sqlBuffer.toString(), new Object[]{}, new Class<?>[]{},dataSourceName);
				
				if(shardingFlag == null){
					//如果是空，没有可用的分片id，需要新增分片记录和分片数据表
					//#1.计算新的分片id
					//那么继续看，最后一条不可用的分片id是多少，倒序排序，取第一条
					sqlBuffer.setLength(0);
					sqlBuffer.append("SELECT sharding_flag FROM ")
					.append(tableSchema.getTableName())
					.append("_sharding_gt WHERE sharding_table_status=0 ORDER BY sharding_flag DESC");//这里不加limit因为标准sql，不能有方言
					shardingFlag = DataBaseHelper.queryPrimitive(sqlBuffer.toString(), new Object[]{}, new Class<?>[]{},dataSourceName);
					//先将entity数据对象的分片标识+1，下面开始构建新的分片数据表，但是由于分片数据表要退出事务后才创建，所以等下还得把分片标识先改回来
					sentity.setShardingFlag(shardingFlag+1);
					
					//#2.新增分片数据表的sql，一定是先增表，后增分片记录，这样如果出现中断，再次走一遍不会出问题
//					sentity.setShardingFlag(shardingFlag);//用于建表
					List<String> shardingTableCreateSqlAry = new ArrayList<>();
					if(DataSourceHelper.isMySql(dataSourceName)){
						String createTableSql = MySqlUtil.getTableCreateSql(dataSourceName,entity);
						shardingTableCreateSqlAry.add(createTableSql);
//						DataBaseHelper.executeUpdate(new String[]{createTableSql}, new Object[]{}, new Class<?>[]{}, dataSourceName);
					}else if(DataSourceHelper.isOracle(dataSourceName)){
						shardingTableCreateSqlAry = OracleUtil.getTableCreateSql(dataSourceName,entity);
//						DataBaseHelper.executeUpdate(sqlAry, new Object[]{}, new Class<?>[]{}, dataSourceName);
					}else {
						throw new Exception(tableSchema.getEntityClass().getName()+" sharding table create task make failed, unspported dbtype driver, only mysql/oracle");
					}
					
					//#3.新增分片记录的sql
					String updateGtTableRecordSql = CommonSqlUtil.getShardingGtTableRecordSql(tableSchema, shardingFlag+1);
					
					//#4.构建新增分片数据表的任务，放在事务之后执行
					ShardingTableCreateTask task = new ShardingTableCreateTask();
					task.setDataSourceName(dataSourceName);
					task.setTableName(tableSchema.getTableName());
					task.setShardingFlag(task.getShardingFlag());
					String[] createDataTableSqlAry = new String[shardingTableCreateSqlAry.size()];
					for(int i=0;i<createDataTableSqlAry.length;i++){
						createDataTableSqlAry[i] = shardingTableCreateSqlAry.get(i);
					}
					task.setCreateDataTableSqlAry(createDataTableSqlAry);
					task.setUpdateGtTableRecordSql(updateGtTableRecordSql);
					TransactionAspect.addShardingTableCreateTask(task);
					
					
					//将数据中的分片标识，改回来，还是存在老的分片数据表，等下次新的分片表好了，再去插入
					sentity.setShardingFlag(shardingFlag);
				}else{
					//如果查到了分片id，直接塞入，准备后续下一步插入
					sentity.setShardingFlag(shardingFlag);
				}
			}
		}
	}
	
	/**
	 * 2018/12/29
	 * 分片数据表状态检测
	 * 在insert和save时候做检测，因为这种情况下会对表的行数产生影响
	 * delete暂时不做检测，一旦表已经被关闭插入，那么就不打开了
	 */
	private void shardingTableStatusCheck(Object entity,String dataSourceName) throws Exception{
		TableSchema tableSchema = TableHelper.getCachedTableSchema(entity);
		
		//只有需要分片的，才处理
		if(tableSchema.getSharding()){
			if(StringUtil.isEmpty(dataSourceName)){
				dataSourceName = tableSchema.getDataSourceName();
			}
			
			//*注意，这里的查询，都是排序后取的第一条，但是没用limit或者top，因为都是方言，所以只需要排序，DataBaseHelper.queryPrimitive默认会返回第一条
			Sharding sentity = (Sharding)entity;
			if(sentity.getShardingFlag() == null){
				throw new Exception("shardingFlag is null");
			}
			
			//查询当前可以插入的表分片的id
			StringBuilder sqlBuffer = new StringBuilder();
			sqlBuffer.append("SELECT count(1) FROM ").append(TableHelper.getRealTableName(entity));
			long row_count = DataBaseHelper.queryPrimitive(sqlBuffer.toString(), new Object[]{}, new Class<?>[]{},dataSourceName);
			sqlBuffer.setLength(0);
			sqlBuffer.append("UPDATE ")
			.append(tableSchema.getTableName())
			.append("_sharding_gt set row_count=").append(row_count);
			if(row_count >= sentity.oneTableMaxCount()){
				sqlBuffer.append(",sharding_table_status=0");
			}
			sqlBuffer.append(" where sharding_flag=").append(sentity.getShardingFlag());
			DataBaseHelper.executeUpdate(new String[]{sqlBuffer.toString()}, new Object[]{}, new Class<?>[]{},dataSourceName);
		}
	}
	
}
