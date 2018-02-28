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
package org.axe.helper.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Transient;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.bean.persistence.InsertResult;
import org.axe.bean.persistence.SqlPackage;
import org.axe.constant.IdGenerateWay;
import org.axe.interface_.base.Helper;
import org.axe.interface_.persistence.BaseDataSource;
import org.axe.util.CastUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 数据库 助手类
 * @author CaiDongyu on 2016/4/15.
 * TODO(OK):增加外部数据源可配置，连接池
 * TODO(OK):自动返回新增主键
 */
public final class DataBaseHelper implements Helper{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseHelper.class);
    
    private static ThreadLocal<HashMap<String,Connection>> CONNECTION_HOLDER;
    
    @Override
    public void init() {
    	synchronized (this) {
    		//#数据库连接池
            CONNECTION_HOLDER = new ThreadLocal<>();
            //#SQL程咬金
            /*Set<Class<?>> sqlCyjClassSet = ClassHelper.getClassSetBySuper(SqlCyj.class);
            if(CollectionUtil.isNotEmpty(sqlCyjClassSet)){
            	if(sqlCyjClassSet.size() > 1){
            		throw new RuntimeException("find "+sqlCyjClassSet.size()+" SqlCyj");
            	}
            	for(Class<?> sqlCyjClass:sqlCyjClassSet){
            		sqlCyj = ReflectionUtil.newInstance(sqlCyjClass);
            		break;
            	}
            }*/
		}
    }

    /**
     * 获取数据库并链接
     * @throws SQLException 
     */
    public static Connection getConnection(String dataSourceName) throws SQLException {
        HashMap<String,Connection> connMap = CONNECTION_HOLDER.get();
        if (connMap == null || !connMap.containsKey(dataSourceName)) {
            try {
            	Map<String, BaseDataSource> dsMap = DataSourceHelper.getDataSourceAll();
        		if(dsMap.containsKey(dataSourceName)){
        			Connection connection = dsMap.get(dataSourceName).getConnection();
        			if(connMap == null){
        				connMap = new HashMap<>();
        				CONNECTION_HOLDER.set(connMap);
        			}
        			connMap.put(dataSourceName, connection);
        		}
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
                throw new SQLException(e);
            }
            
        }
        if(connMap != null && connMap.containsKey(dataSourceName)){
        	return connMap.get(dataSourceName);
        }else{
        	throw new RuntimeException("connot find connection of dataSource:"+dataSourceName);
        }
    }

    /**
     * 关闭链接
     * @throws SQLException 
     */
    public static void closeConnection(String dataSourceName) throws SQLException {
    	HashMap<String, Connection> connMap = CONNECTION_HOLDER.get();
    	try {
    		do{
    			if(connMap == null) break;
    			Connection con = connMap.get(dataSourceName);
    			if(con == null) break;
				if(!con.isClosed()){
					con.close();
					LOGGER.debug("release connection of dataSource["+dataSourceName+"]:"+con);
				}
    		}while(false);
		} catch (SQLException e) {
			LOGGER.error("release connection of dataSource["+dataSourceName+"] failure", e);
			throw new SQLException(e);
		}  finally {
			if(connMap != null){
				boolean isAllConClosed = true;
				for(Connection con:connMap.values()){
					if(!con.isClosed()){
						isAllConClosed = false;
						break;
					}
				}
				if(isAllConClosed){
					CONNECTION_HOLDER.remove();
					LOGGER.debug("clean CONNECTION_HOLDER");
				}
			}
        }
    }
    
    /**
     * sql前去数据库路上的终点站，出了这个方法，就是奈何桥了。
     */
    private static PreparedStatement getPrepareStatement(Connection conn, String sql, Object[] params, Class<?>[] paramTypes,boolean RETURN_GENERATED_KEYS) throws SQLException{
    	SqlPackage sp = SqlHelper.convertGetFlag(sql, params, paramTypes);
    	//半路杀出个“程咬金”
    	/*if(sqlCyj != null){
    		sp = sqlCyj.robSqlPackage(sp);
    	}*/
    	//打印调试sql
    	SqlHelper.debugSql(sp);
    	PreparedStatement ps = null;
    	if(RETURN_GENERATED_KEYS){
    		ps = conn.prepareStatement(sp.getSql(), Statement.RETURN_GENERATED_KEYS);
    	}else{
    		ps = conn.prepareStatement(sp.getSql());
    	}
    	for(int parameterIndex=1;parameterIndex<=sp.getParams().length;parameterIndex++){
    		ps.setObject(parameterIndex, sp.getParams()[parameterIndex-1]);
    	}
    	return ps;
    }
    
    public static <T> List<T> queryEntityList(final Class<T> entityClass, String sql, Object[] params, Class<?>[] paramTypes) throws SQLException {
    	   String dataSourceName = TableHelper.getTableDataSourceName(entityClass);
    	   return queryEntityList(entityClass, sql, params, paramTypes, dataSourceName);
    }
    
    /**
     * 查询实体列表
     * @throws SQLException 
     */
	public static <T> List<T> queryEntityList(final Class<T> entityClass, String sql, Object[] params, Class<?>[] paramTypes, String dataSourceName) throws SQLException {
        List<T> entityList = new ArrayList<>();
        Connection conn = getConnection(dataSourceName);
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes, false);
        	ResultSet table = ps.executeQuery();
        	List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getSetMethodList(entityClass);
			Set<String> transientField = new HashSet<>();
        	while(table.next()){
				T entity = ReflectionUtil.newInstance(entityClass);
				for(EntityFieldMethod entityFieldMethod:entityFieldMethodList){
					Field field = entityFieldMethod.getField();
					String fieldName = field.getName();
					if(transientField.contains(fieldName)){
						continue;
					}
					if(field.isAnnotationPresent(Transient.class)){
						if(!field.getAnnotation(Transient.class).query()){
							transientField.add(fieldName);
							continue;
						}
					}
					Method method = entityFieldMethod.getMethod();
					String columnName = StringUtil.camelToUnderline(fieldName);
					try {
						Object setMethodArg = CastUtil.castType(table.getObject(columnName),field.getType());
						ReflectionUtil.invokeMethod(entity, method, setMethodArg);
					} catch (SQLException e) {
						if(e.getMessage().contains("Column '"+columnName+"' not found")){
							//字段不存在情况可以不处理
						}else{
							//其他异常抛出
							throw e;
						}
					}
				}
				entityList.add(entity);
			}
			table.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new SQLException(e);
        } finally {
            if(conn.getAutoCommit()){
            	closeConnection(dataSourceName);
            }
        }
        return entityList;
    }

	public static <T> T queryEntity(final Class<T> entityClass, String sql, Object[] params, Class<?>[] paramTypes) throws SQLException {
        String dataSourceName = TableHelper.getTableDataSourceName(entityClass);
        return queryEntity(entityClass, sql, params, paramTypes, dataSourceName);
	}
	
    /**
     * 查询单个实体
     * @throws SQLException 
     */
    public static <T> T queryEntity(final Class<T> entityClass, String sql, Object[] params, Class<?>[] paramTypes, String dataSourceName) throws SQLException {
        T entity = null;
        Connection conn = getConnection(dataSourceName);
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes, false);
        	ResultSet table = ps.executeQuery();
        	if(table.next()){
    			List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getSetMethodList(entityClass);
    			entity = ReflectionUtil.newInstance(entityClass);
    			for(EntityFieldMethod entityFieldMethod:entityFieldMethodList){
					Field field = entityFieldMethod.getField();
					String fieldName = field.getName();
					if(field.isAnnotationPresent(Transient.class)){
						if(!field.getAnnotation(Transient.class).query()){
							continue;
						}
					}
					Method method = entityFieldMethod.getMethod();
					String columnName = StringUtil.camelToUnderline(fieldName);
					try {
						Object setMethodArg = CastUtil.castType(table.getObject(columnName),field.getType());
						ReflectionUtil.invokeMethod(entity, method, setMethodArg);
					} catch (SQLException e) {
						if(e.getMessage().contains("Column '"+columnName+"' not found")){
							//字段不存在情况可以不处理
						}else{
							//其他异常抛出
							throw e;
						}
					}
				}
			}
			table.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new SQLException(e);
        } finally {
            if(conn.getAutoCommit()){
                closeConnection(dataSourceName);
            }
        }
        return entity;
    }

    public static List<Map<String, Object>> queryList(String sql, Object[] params, Class<?>[] paramTypes) throws SQLException {
        String dataSourceName = TableHelper.getTableDataSourceName(null);
    	return queryList(sql, params, paramTypes, dataSourceName);
    }
    
    /**
     * 执行List查询
     * @throws SQLException 
     */
    public static List<Map<String, Object>> queryList(String sql, Object[] params, Class<?>[] paramTypes, String dataSourceName) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = getConnection(dataSourceName);
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes, false);
        	ResultSet table = ps.executeQuery();
        	ResultSetMetaData rsmd = ps.getMetaData();
			while(table.next()){
				Map<String, Object> row = new HashMap<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					row.put(rsmd.getColumnLabel(i), table.getObject(i));
	        	}
				result.add(row);
			}
			table.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("execute queryList failure", e);
            throw new SQLException(e);
        } finally {
            if(conn.getAutoCommit()){
                closeConnection(dataSourceName);
            }
        }
        return result;
    }
    
    public static Map<String, Object> queryMap(String sql, Object[] params, Class<?>[] paramTypes) throws SQLException {
        String dataSourceName = TableHelper.getTableDataSourceName(null);
        return queryMap(sql, params, paramTypes, dataSourceName);
    }
    
    /**
     * 执行单条查询
     * @throws SQLException 
     */
    public static Map<String, Object> queryMap(String sql, Object[] params, Class<?>[] paramTypes, String dataSourceName) throws SQLException {
        Map<String, Object> result = null;
        Connection conn = getConnection(dataSourceName);
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes, false);
        	ResultSet table = ps.executeQuery();
        	ResultSetMetaData rsmd = ps.getMetaData();
        	if(table.next()){
        		result = new HashMap<>();
        		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        			result.put(rsmd.getColumnLabel(i), table.getObject(i));
	        	}
			}
			table.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("execute queryMap failure", e);
            throw new SQLException(e);
        } finally {
            if(conn.getAutoCommit()){
                closeConnection(dataSourceName);
            }
        }
        return result;
    }

	public static <T> T queryPrimitive(String sql, Object[] params, Class<?>[] paramTypes) throws SQLException {
    	String dataSourceName = TableHelper.getTableDataSourceName(null);
    	return queryPrimitive(sql, params, paramTypes, dataSourceName);
	}

    /**
     * 执行返回结果是基本类型的查询
     * @throws SQLException 
     */
	@SuppressWarnings("unchecked")
	public static <T> T queryPrimitive(String sql, Object[] params, Class<?>[] paramTypes, String dataSourceName) throws SQLException {
    	T result = null;
        Connection conn = getConnection(dataSourceName);
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes, false);
        	ResultSet table = ps.executeQuery();
        	if(table.next()){
            	ResultSetMetaData rsmd = ps.getMetaData();
            	if(rsmd.getColumnCount() > 0);
        			result = (T)table.getObject(1);
			}
        	
			table.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("execute queryPrimitive failure", e);
            throw new SQLException(e);
        } finally {
            if(conn.getAutoCommit()){
                closeConnection(dataSourceName);
            }
        }
        return result;
    }
    

	public static long countQuery(String sql, Object[] params, Class<?>[] paramTypes) {
		String dataSourceName = TableHelper.getTableDataSourceName(null);
		return countQuery(sql, params, paramTypes, dataSourceName);
	}
	
    /**
     * 执行返回结果是基本类型的查询
     */
    public static long countQuery(String sql, Object[] params, Class<?>[] paramTypes,String dataSourceName) {
    	long result = 0;
        try {
        	//包装count(1)语句
        	sql = SqlHelper.convertSqlCount(sql);
        	
        	//免转换，因为queryPrimitive会做
//        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params, paramTypes);
            result = queryPrimitive(sql, params, paramTypes, dataSourceName);
        } catch (Exception e) {
            LOGGER.error("execute countQuery failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }
    
    public static int executeUpdate(String sql, Object[] params, Class<?>[] paramTypes) throws SQLException {
        String dataSourceName = TableHelper.getTableDataSourceName(null);
        return executeUpdate(sql, params, paramTypes, dataSourceName);
    }

    /**
     * 执行更新语句 （包括 update、delete）
     * @throws SQLException 
     */
    public static int executeUpdate(String sql, Object[] params, Class<?>[] paramTypes, String dataSourceName) throws SQLException {
        int rows = 0;
        Connection conn = getConnection(dataSourceName);
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes, false);
        	rows = ps.executeUpdate();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new SQLException(e);
        } finally {
            if(conn.getAutoCommit()){
                closeConnection(dataSourceName);
            }
        }
        return rows;
    }
    
    /*private static InsertResult executeInsert(String sql, Object[] params, Class<?>[] paramTypes) throws SQLException {
    	String dataSourceName = TableHelper.getTableDataSourceName(null);
    	return executeInsert(sql, params, paramTypes, dataSourceName);
    }*/
    
    /**
     * 执行插入语句 insert
     * 与executeUpdate类似，只是需要返回主键
     * @throws SQLException 
     */
    private static InsertResult executeInsert(String sql, Object[] params, Class<?>[] paramTypes, String dataSourceName) throws SQLException {
        int rows = 0;
        Object generatedKey = null;
        Connection conn = getConnection(dataSourceName);
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes, true);
        	rows = ps.executeUpdate();
        	ResultSet rs = ps.getGeneratedKeys();
        	if(rs.next()){
        		generatedKey = rs.getObject(1);
        	}
        	rs.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("execute insert failure", e);
            throw new SQLException(e);
        } finally {
            if(conn.getAutoCommit()){
                closeConnection(dataSourceName);
            }
        }
        return new InsertResult(rows, generatedKey);
    }


    public static <T> T insertEntity(T entity) throws SQLException {
    	String dataSourceName = TableHelper.getTableDataSourceName(entity.getClass());
    	return insertEntity(entity, dataSourceName);
    }
    
    /**
     * 插入实体
     * @throws SQLException 
     */
    public static <T> T insertEntity(T entity,String dataSourceName) throws SQLException {
    	if(entity == null)
    		throw new RuntimeException("insertEntity failure, insertEntity param is null!");
    	SqlPackage sp = SqlHelper.getInsertSqlPackage(entity);
    	InsertResult executeInsert = executeInsert(sp.getSql(), sp.getParams(), sp.getParamTypes(),dataSourceName);
    	do{
    		if(executeInsert.getEffectedRows() <= 0) break;
    		//插入成功
    		Object generatedKey = executeInsert.getGeneratedKey();
    		if(generatedKey != null){
    			List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
    			for(EntityFieldMethod entityFieldMethod : entityFieldMethodList){
    				Field field = entityFieldMethod.getField();
    				if(field.isAnnotationPresent(Id.class) && field.getAnnotation(Id.class).idGenerateWay().equals(IdGenerateWay.AUTO_INCREMENT)){
    					Method method = entityFieldMethod.getMethod();
    					Object idValue = ReflectionUtil.invokeMethod(entity, method);
    					if(idValue == null){
    						//如果id字段没有值，就用返回的自增主键赋值
    						Object setMethodArg = CastUtil.castType(executeInsert.getGeneratedKey(),field.getType());
    						ReflectionUtil.setField(entity, field, setMethodArg);
    					}
    					break;
    				}
    			}
    		}
            return entity;
    	}while(false);
    	return null;
    }
    

    public static int updateEntity(Object entity) throws SQLException {
    	String dataSourceName = TableHelper.getTableDataSourceName(entity.getClass());
    	return updateEntity(entity, dataSourceName);
    }

    /**
     * 更新实体
     * @throws SQLException 
     */
    public static int updateEntity(Object entity,String dataSourceName) throws SQLException {
    	if(entity == null)
    		throw new RuntimeException("updateEntity failure, updateEntity param is null!");
        SqlPackage sp = SqlHelper.getUpdateSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams(), sp.getParamTypes(), dataSourceName);
    }
    

    public static <T> T insertOnDuplicateKeyEntity(T entity) throws SQLException {
    	String dataSourceName = TableHelper.getTableDataSourceName(entity.getClass());
    	return insertOnDuplicateKeyEntity(entity, dataSourceName);
    }
    
    /**
     * 更新实体
     * @throws SQLException 
     */
    public static <T> T insertOnDuplicateKeyEntity(T entity,String dataSourceName) throws SQLException {
    	if(entity == null)
    		throw new RuntimeException("insertOnDuplicateKeyEntity failure, insertOnDuplicateKeyEntity param is null!");
        SqlPackage sp = SqlHelper.getInsertOnDuplicateKeyUpdateSqlPackage(entity);
        InsertResult executeInsert = executeInsert(sp.getSql(), sp.getParams(), sp.getParamTypes(), dataSourceName);
    	do{
    		if(executeInsert.getEffectedRows() <= 0) break;
    		//插入成功
    		if(executeInsert.getGeneratedKey() != null){
    			List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
    			for(EntityFieldMethod entityFieldMethod : entityFieldMethodList){
    				Field field = entityFieldMethod.getField();
    				if(field.isAnnotationPresent(Id.class)){
    					Method method = entityFieldMethod.getMethod();
    					Object idValue = ReflectionUtil.invokeMethod(entity, method);
    					if(!executeInsert.getGeneratedKey().equals(idValue)){
    						//如果id字段没有值，就用返回的自增主键赋值
    						Object setMethodArg = CastUtil.castType(executeInsert.getGeneratedKey(),field.getType());
    						ReflectionUtil.setField(entity, field, setMethodArg);
    					}
    					break;
    				}
    			}
    		}
            return entity;
    	}while(false);
    	return null;
    }

    public static int deleteEntity(Object entity) throws SQLException {
    	String dataSourceName = TableHelper.getTableDataSourceName(entity.getClass());
    	return deleteEntity(entity,dataSourceName);
    }

    /**
     * 删除实体
     * @throws SQLException 
     */
    public static int deleteEntity(Object entity,String dataSourceName) throws SQLException {
    	if(entity == null)
    		throw new RuntimeException("deleteEntity failure, deleteEntity param is null!");
        SqlPackage sp = SqlHelper.getDeleteSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams(), sp.getParamTypes(), dataSourceName);
    }
    

	public static <T> T getEntity(T entity) throws SQLException{
    	String dataSourceName = TableHelper.getTableDataSourceName(entity.getClass());
		return getEntity(entity, dataSourceName);
	}
    
    @SuppressWarnings("unchecked")
	public static <T> T getEntity(T entity,String dataSourceName) throws SQLException{
    	if(entity == null)
    		throw new RuntimeException("getEntity failure, getEntity param is null!");
        SqlPackage sp = SqlHelper.getSelectByIdSqlPackage(entity);
        entity = (T)queryEntity(entity.getClass(), sp.getSql(), sp.getParams(), sp.getParamTypes(),dataSourceName);
    	return (T)entity;
    }

    /**
     * 开启事务
     * @throws SQLException 
     */
    public static void beginTransaction() throws SQLException{
//    	long t = System.currentTimeMillis();
//		System.out.println("e1:"+t);
    	Map<String, BaseDataSource> dsMap = DataSourceHelper.getDataSourceAll();
    	HashMap<String, Connection> connMap = new HashMap<>();
        try {
        	for(String dataSourceName:dsMap.keySet()){
        		BaseDataSource dataSource = dsMap.get(dataSourceName);
//        		t = System.currentTimeMillis();
//        		System.out.println("e2-"+dataSourceName+":"+t);
        		if(dataSource.tns()){
        			Connection conn = dataSource.getConnection();
        			conn.setAutoCommit(false);//设置成手动提交
        			connMap.put(dataSourceName, conn);
        		}
//        		t = System.currentTimeMillis();
//        		System.out.println("e2-"+dataSourceName+":"+t);
        	}
        	CONNECTION_HOLDER.set(connMap);
        } catch (SQLException e){
            LOGGER.error("begin transaction failure",e);
            throw new SQLException(e);
        }
//        t = System.currentTimeMillis();
//		System.out.println("e3:"+t);
    }

    /**
     * 提交事务
     * @throws SQLException 
     */
    public static void commitTransaction() throws SQLException{
    	HashMap<String, Connection> connMap = CONNECTION_HOLDER.get();
    	Map<String, BaseDataSource> dsMap = DataSourceHelper.getDataSourceAll();
        if(connMap != null && connMap.size() > 0){
        	String errorDataSourceName = null;
            try {
            	for(String dataSourceName:connMap.keySet()){
            		if(dsMap.get(dataSourceName).tns()){
            			errorDataSourceName = dataSourceName;
            			Connection conn = connMap.get(dataSourceName);
            			if(!conn.getAutoCommit()){
            				conn.commit();
            			}
            		}
            	}
            } catch (SQLException e){
                LOGGER.error("commit transaction of dataSource["+errorDataSourceName+"] failure",e);
                throw new SQLException(e);
            }finally {
            	for(String dataSourceName:connMap.keySet()){
            		if(dsMap.get(dataSourceName).tns()){
            			if(!connMap.get(dataSourceName).getAutoCommit()){
            				closeConnection(dataSourceName);
            			}
            		}
            	}
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(){
    	HashMap<String, Connection> connMap = CONNECTION_HOLDER.get();
    	Map<String, BaseDataSource> dsMap = DataSourceHelper.getDataSourceAll();
        if(connMap != null && connMap.size() > 0){
        	String errorDataSourceName = null;
            try {
            	for(String dataSourceName:connMap.keySet()){
            		if(dsMap.get(dataSourceName).tns()){
            			errorDataSourceName = dataSourceName;
                		Connection conn = connMap.get(dataSourceName);
    	            	conn.rollback();
            		}
            	}
            } catch (SQLException e){
                LOGGER.error("rollback transaction of dataSource["+errorDataSourceName+"] failure",e);
                throw new RuntimeException(e);
            } finally {
                try {
                	for(String dataSourceName:connMap.keySet()){
                		if(dsMap.get(dataSourceName).tns()){
                			if(!connMap.get(dataSourceName).getAutoCommit()){
                				closeConnection(dataSourceName);
                			}
                		}
                	}
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

	@Override
	public void onStartUp() throws Exception {}
    
}
