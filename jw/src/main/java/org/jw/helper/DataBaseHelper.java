package org.jw.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.jw.bean.EntityFieldMethod;
import org.jw.bean.SqlPackage;
import org.jw.util.ReflectionUtil;
import org.jw.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库 助手类
 * Created by CaiDongYu on 2016/4/15.
 */
public class DataBaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseHelper.class);
    
    
    //#数据库
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final ThreadLocal<Connection> CONNECTION_HOLDER;

    private static final QueryRunner QUERY_RUNNER;

    private static final BasicDataSource DATA_SOURCE;

    static {

        //#初始化jdbc配置
        DRIVER = ConfigHelper.getJdbcDriver();
        URL = ConfigHelper.getJdbcUrl();
        USERNAME = ConfigHelper.getJdbcUsername();
        PASSWORD = ConfigHelper.getJdbcPassword();
        //#数据库连接池
        CONNECTION_HOLDER = new ThreadLocal<>();
        QUERY_RUNNER = new QueryRunner();
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

    /**
     * 查询实体列表
     */
	public static <T> List<T> queryEntityList(final Class<T> entityClass, String sql, Object... params) {
		LOGGER.debug(sql);
        List<T> entityList;
        Connection conn = getConnection();
        try {
        	//BeanListHandler 不支持Date，所以自己实现
            //entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<>(entityClass), params);
        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params);
        	entityList = QUERY_RUNNER.query(conn, sp.getSql(), new ResultSetHandler<List<T>>(){
        		@Override
        		public List<T> handle(ResultSet table) throws SQLException {
        			List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getSetMethodList(entityClass);
					List<T> list = new ArrayList<>();
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
						list.add(entity);
					}
					return list;
        		}
        	},sp.getParams());
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
    public static <T> T queryEntity(final Class<T> entityClass, String sql, Object... params) {
		LOGGER.debug(sql);
        T entity;
        Connection conn = getConnection();
        try {
        	//BeanHandler 不支持Date，所以自己实现
        	//entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<>(entityClass), params);
        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params);
        	entity = QUERY_RUNNER.query(conn, sp.getSql(), new ResultSetHandler<T>(){

				@Override
				public T handle(ResultSet table) throws SQLException {
					if(table.next()){
	        			List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getSetMethodList(entityClass);
						T entity = ReflectionUtil.newInstance(entityClass);
						for(EntityFieldMethod entityFieldMethod:entityFieldMethodList){
							Field field = entityFieldMethod.getField();
							Method method = entityFieldMethod.getMethod();
							String fieldName = field.getName();
							String columnName = StringUtil.camelToUnderline(fieldName);
							Object setMethodArg = table.getObject(columnName);
							ReflectionUtil.invokeMethod(entity, method, setMethodArg);
						}
						return entity;
					}
					return null;
				}
        	}, sp.getParams());
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
    public static List<Map<String, Object>> queryList(String sql, Object... params) {
		LOGGER.debug(sql);
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = getConnection();
        try {
        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params);
            result = QUERY_RUNNER.query(conn, sp.getSql(), new MapListHandler(), sp.getParams());
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
    public static Map<String, Object> queryMap(String sql, Object... params) {
		LOGGER.debug(sql);
        Map<String, Object> result = new HashMap<>();
        Connection conn = getConnection();
        try {
        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params);
            result = QUERY_RUNNER.query(conn, sp.getSql(), new MapHandler(), sp.getParams());
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
    public static <T> T queryPrimitive(String sql, Object... params) {
		LOGGER.debug(sql);
    	T result = null;
        Connection conn = getConnection();
        try {
        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params);
            result = QUERY_RUNNER.query(conn, sp.getSql(), new ScalarHandler<T>(), sp.getParams());
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
     * 执行更新语句 （包括 update、insert、delete）
     */
    public static int executeUpdate(String sql, Object... params) {
		LOGGER.debug(sql);
        int rows = 0;
        Connection conn = getConnection();
        try {
        	SqlPackage sp = SqlHelper.convertGetFlag(sql, params);
            rows = QUERY_RUNNER.update(conn, sp.getSql(), sp.getParams());
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
    	SqlPackage sp = SqlHelper.getInsertSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams());
    }

    /**
     * 更新实体
     */
    public static int updateEntity(Object entity) {
        SqlPackage sp = SqlHelper.getUpdateSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams());
    }
    
    /**
     * 更新实体
     */
    public static int insertOnDuplicateKeyEntity(Object entity) {
        SqlPackage sp = SqlHelper.getInsertOnDuplicateKeyUpdateSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams());
    }


    /**
     * 删除实体
     */
    public static int deleteEntity(Object entity) {
        SqlPackage sp = SqlHelper.getDeleteSqlPackage(entity);
        return executeUpdate(sp.getSql(), sp.getParams());
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getEntity(Object entity){
        SqlPackage sp = SqlHelper.getSelectSqlPackage(entity);
    	return (T)queryEntity(entity.getClass(), sp.getSql(), sp.getParams());
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
