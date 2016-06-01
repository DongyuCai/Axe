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
import java.util.List;
import java.util.Map;

import org.axe.annotation.persistence.Id;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.bean.persistence.InsertResult;
import org.axe.bean.persistence.SqlPackage;
import org.axe.helper.Helper;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 数据库 助手类
 * Created by CaiDongYu on 2016/4/15.
 * TODO(OK):增加外部数据源可配置，连接池
 * TODO(OK):自动返回新增主键
 */
public final class DataBaseHelper implements Helper{

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseHelper.class);
    
    private static ThreadLocal<Connection> CONNECTION_HOLDER;

    @Override
    public void init() {
    	synchronized (this) {
    		 //#数据库连接池
            CONNECTION_HOLDER = new ThreadLocal<>();
		}
    }

    
    /**
     * 获取数据库并链接
     */
    public static Connection getConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            try {
                conn = DataSourceHelper.getDataSource().getConnection();
                LOGGER.debug("get connection:"+conn);
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

    /**
     * 关闭链接
     */
    public static void closeConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn != null) {
            try {
            	if(!conn.isClosed()){
            		conn.close();
                    LOGGER.debug("release connection:"+conn);
            	}
            } catch (SQLException e) {
                LOGGER.error("release connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }
    
    private static PreparedStatement getPrepareStatement(Connection conn, String sql, Object[] params, Class<?>[] paramTypes) throws SQLException{
    	SqlPackage sp = SqlHelper.convertGetFlag(sql, params, paramTypes);
    	PreparedStatement ps = conn.prepareStatement(sp.getSql());
    	for(int parameterIndex=1;parameterIndex<=sp.getParams().length;parameterIndex++){
    		ps.setObject(parameterIndex, sp.getParams()[parameterIndex-1]);
    	}
    	return ps;
    }
    
    /**
     * 查询实体列表
     */
	public static <T> List<T> queryEntityList(final Class<T> entityClass, String sql, Object[] params, Class<?>[] paramTypes) {
		LOGGER.debug(sql);
        List<T> entityList = new ArrayList<>();
        Connection conn = getConnection();
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes);
        	ResultSet table = ps.executeQuery();
        	List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getSetMethodList(entityClass);
			while(table.next()){
				T entity = ReflectionUtil.newInstance(entityClass);
				for(EntityFieldMethod entityFieldMethod:entityFieldMethodList){
					Field field = entityFieldMethod.getField();
					Method method = entityFieldMethod.getMethod();
					String fieldName = field.getName();
					String columnName = StringUtil.camelToUnderline(fieldName);
					Object setMethodArg = table.getObject(columnName);
					ReflectionUtil.invokeMethod(entity, method, setMethodArg);
				}
				entityList.add(entity);
			}
			table.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(conn.getAutoCommit()){
                    closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return entityList;
    }

    /**
     * 查询单个实体
     */
    public static <T> T queryEntity(final Class<T> entityClass, String sql, Object[] params, Class<?>[] paramTypes) {
		LOGGER.debug(sql);
        T entity = null;
        Connection conn = getConnection();
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes);
        	ResultSet table = ps.executeQuery();
        	if(table.next()){
    			List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getSetMethodList(entityClass);
    			entity = ReflectionUtil.newInstance(entityClass);
				for(EntityFieldMethod entityFieldMethod:entityFieldMethodList){
					Field field = entityFieldMethod.getField();
					Method method = entityFieldMethod.getMethod();
					String fieldName = field.getName();
					String columnName = StringUtil.camelToUnderline(fieldName);
					Object setMethodArg = table.getObject(columnName);
					ReflectionUtil.invokeMethod(entity, method, setMethodArg);
				}
			}
			table.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(conn.getAutoCommit()){
                    closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }

    /**
     * 执行List查询
     */
    public static List<Map<String, Object>> queryList(String sql, Object[] params, Class<?>[] paramTypes) {
		LOGGER.debug(sql);
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = getConnection();
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes);
        	ResultSet table = ps.executeQuery();
        	ResultSetMetaData rsmd = ps.getMetaData();
			while(table.next()){
				Map<String, Object> row = new HashMap<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					row.put(rsmd.getColumnName(i), table.getObject(i));
	        	}
				result.add(row);
			}
			table.close();
			ps.close();
        } catch (Exception e) {
            LOGGER.error("execute queryList failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(conn.getAutoCommit()){
                    closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    
    /**
     * 执行单条查询
     */
    public static Map<String, Object> queryMap(String sql, Object[] params, Class<?>[] paramTypes) {
		LOGGER.debug(sql);
        Map<String, Object> result = null;
        Connection conn = getConnection();
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes);
        	ResultSet table = ps.executeQuery();
        	ResultSetMetaData rsmd = ps.getMetaData();
        	if(table.next()){
        		result = new HashMap<>();
        		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        			result.put(rsmd.getColumnName(i), table.getObject(i));
	        	}
			}
			table.close();
			ps.close();
        } catch (Exception e) {
            LOGGER.error("execute queryMap failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(conn.getAutoCommit()){
                    closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    

    /**
     * 执行返回结果是基本类型的查询
     */
    @SuppressWarnings("unchecked")
	public static <T> T queryPrimitive(String sql, Object[] params, Class<?>[] paramTypes) {
		LOGGER.debug(sql);
    	T result = null;
        Connection conn = getConnection();
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes);
        	ResultSet table = ps.executeQuery();
        	if(table.next()){
            	ResultSetMetaData rsmd = ps.getMetaData();
            	if(rsmd.getColumnCount() > 0);
        			result = (T)table.getObject(1);
			}
			table.close();
			ps.close();
        } catch (Exception e) {
            LOGGER.error("execute queryPrimitive failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(conn.getAutoCommit()){
                    closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    

    /**
     * 执行返回结果是基本类型的查询
     */
    public static long countQuery(String sql, Object[] params, Class<?>[] paramTypes) {
    	long result = 0;
        try {
        	//包装count(1)语句
        	sql = "select count(1) from("+sql+") as table_"+System.currentTimeMillis();
        	
        	//免转换，因为queryPrimitive会做
//        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params, paramTypes);
            result = queryPrimitive(sql, params, null);
        } catch (Exception e) {
            LOGGER.error("execute countQuery failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }
    


    /**
     * 执行更新语句 （包括 update、delete）
     */
    public static int executeUpdate(String sql, Object[] params, Class<?>[] paramTypes) {
		LOGGER.debug(sql);
        int rows = 0;
        Connection conn = getConnection();
        try {
        	PreparedStatement ps = getPrepareStatement(conn, sql, params, paramTypes);
        	rows = ps.executeUpdate();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(conn.getAutoCommit()){
                    closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return rows;
    }
    
    /**
     * 执行插入语句 insert
     * 与executeUpdate类似，只是需要返回主键
     */
    public static InsertResult executeInsert(String sql, Object[] params, Class<?>[] paramTypes) {
		LOGGER.debug(sql);
        int rows = 0;
        Object generatedKey = null;
        Connection conn = getConnection();
        try {
        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params, paramTypes);
        	PreparedStatement ps = conn.prepareStatement(sp.getSql(), Statement.RETURN_GENERATED_KEYS);
        	for(int parameterIndex=1;parameterIndex<=sp.getParams().length;parameterIndex++){
        		ps.setObject(parameterIndex, sp.getParams()[parameterIndex-1]);
        	}
        	rows = ps.executeUpdate();
        	ResultSet rs = ps.getGeneratedKeys();
        	if(rs.next()){
        		generatedKey = rs.getObject(1);
        	}
        	rs.close();
			ps.close();
        } catch (SQLException e) {
            LOGGER.error("execute insert failure", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if(conn.getAutoCommit()){
                    closeConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return new InsertResult(rows, generatedKey);
    }

    /**
     * 插入实体
     */
    public static <T> T insertEntity(T entity) {
    	if(entity == null)
    		throw new RuntimeException("insertEntity failure, insertEntity param is null!");
    	SqlPackage sp = SqlHelper.getInsertSqlPackage(entity);
    	InsertResult executeInsert = executeInsert(sp.getSql(), sp.getParams(), sp.getParamTypes());
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
    						ReflectionUtil.setField(entity, field, executeInsert.getGeneratedKey());
    					}
    					break;
    				}
    			}
    		}
            return entity;
    	}while(false);
    	return null;
    }

    /**
     * 更新实体
     */
    public static int updateEntity(Object entity) {
    	if(entity == null)
    		throw new RuntimeException("updateEntity failure, updateEntity param is null!");
        SqlPackage sp = SqlHelper.getUpdateSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams(), sp.getParamTypes());
    }
    
    /**
     * 更新实体
     */
    public static <T> T insertOnDuplicateKeyEntity(T entity) {
    	if(entity == null)
    		throw new RuntimeException("insertOnDuplicateKeyEntity failure, insertOnDuplicateKeyEntity param is null!");
        SqlPackage sp = SqlHelper.getInsertOnDuplicateKeyUpdateSqlPackage(entity);
        InsertResult executeInsert = executeInsert(sp.getSql(), sp.getParams(), sp.getParamTypes());
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
    						ReflectionUtil.setField(entity, field, executeInsert.getGeneratedKey());
    					}
    					break;
    				}
    			}
    		}
            return entity;
    	}while(false);
    	return null;
    }


    /**
     * 删除实体
     */
    public static int deleteEntity(Object entity) {
    	if(entity == null)
    		throw new RuntimeException("deleteEntity failure, deleteEntity param is null!");
        SqlPackage sp = SqlHelper.getDeleteSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams(), sp.getParamTypes());
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getEntity(T entity){
    	if(entity == null)
    		throw new RuntimeException("getEntity failure, getEntity param is null!");
        SqlPackage sp = SqlHelper.getSelectByIdSqlPackage(entity);
        entity = (T)queryEntity(entity.getClass(), sp.getSql(), sp.getParams(), sp.getParamTypes());
    	return (T)entity;
    }

    /**
     * 开启事务
     */
    public static void beginTransaction(){
        Connection conn = getConnection();
        if(conn != null){
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e){
                LOGGER.error("begin transaction failure",e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction(){
        Connection conn = getConnection();
        if(conn != null){
            try {
            	if(!conn.getAutoCommit()){
            		conn.commit();
            	}
            } catch (SQLException e){
                LOGGER.error("commit transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                try {
                    if(!conn.getAutoCommit()){
                        closeConnection();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(){
        Connection conn = getConnection();
        if (conn != null){
            try {
                conn.rollback();
            } catch (SQLException e){
                LOGGER.error("rollback transaction failure",e);
                throw new RuntimeException(e);
            } finally {
                try {
                    if(!conn.getAutoCommit()){
                        closeConnection();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
}
