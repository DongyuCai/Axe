package org.jw.helper.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jw.bean.persistence.EntityFieldMethod;
import org.jw.bean.persistence.SqlPackage;
import org.jw.helper.base.ConfigHelper;
import org.jw.util.ReflectionUtil;
import org.jw.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库 助手类
 * Created by CaiDongYu on 2016/4/15.
 * TODO:增加外部数据源可配置，连接池
 */
public class DataBaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseHelper.class);
    
    
    //#数据库
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final ThreadLocal<Connection> CONNECTION_HOLDER;

    private static final BasicDataSource DATA_SOURCE;

    static {

        //#初始化jdbc配置
        DRIVER = ConfigHelper.getJdbcDriver();
        URL = ConfigHelper.getJdbcUrl();
        USERNAME = ConfigHelper.getJdbcUsername();
        PASSWORD = ConfigHelper.getJdbcPassword();
        //#数据库连接池
        CONNECTION_HOLDER = new ThreadLocal<>();
        DATA_SOURCE = new BasicDataSource();

        try {
            DATA_SOURCE.setDriverClassName(DRIVER);
            DATA_SOURCE.setUrl(URL);
            DATA_SOURCE.setUsername(USERNAME);
            DATA_SOURCE.setPassword(PASSWORD);
            
            //TODO:启动时同步表结构，（现阶段不会开发此功能，为了支持多数据源，借鉴了Rose框架）
        } catch (Exception e) {
            LOGGER.error("jdbc driver : " + DRIVER);
            LOGGER.error("jdbc url : " + URL);
            LOGGER.error("jdbc username : " + USERNAME);
            LOGGER.error("jdbc password : " + PASSWORD);
            LOGGER.error("load jdbc driver failure", e);
        }
        
    }

    
    /**
     * 获取数据库并链接
     */
    public static Connection getConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            try {
                conn = DATA_SOURCE.getConnection();
                LOGGER.debug("create connection:"+conn);
            } catch (SQLException e) {
                LOGGER.error("get jdbc connection failure", e);
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
            	}
            } catch (SQLException e) {
                LOGGER.error("close jdbc connection failure", e);
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
     * 插入实体
     */
    public static int insertEntity(Object entity) {
    	if(entity == null)
    		throw new RuntimeException("insertEntity failure, insertEntity param is null!");
    	SqlPackage sp = SqlHelper.getInsertSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams(), sp.getParamTypes());
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
    public static int insertOnDuplicateKeyEntity(Object entity) {
    	if(entity == null)
    		throw new RuntimeException("insertOnDuplicateKeyEntity failure, insertOnDuplicateKeyEntity param is null!");
        SqlPackage sp = SqlHelper.getInsertOnDuplicateKeyUpdateSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams(), sp.getParamTypes());
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
