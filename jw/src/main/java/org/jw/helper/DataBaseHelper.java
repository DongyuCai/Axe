package org.jw.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.jw.annotation.Id;
import org.jw.annotation.Table;
import org.jw.bean.EntityFieldMethod;
import org.jw.util.CollectionUtil;
import org.jw.util.JsonUtil;
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

    
    //#@Table 实体
    private static final Map<String,Class<?>> ENTITY_CLASS_MAP = new HashMap<>();
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
        
        //#加载所有@Table指定的Entity类
        Set<Class<?>> entityClassSet = ClassHelper.getClassSetByAnnotation(Table.class);
        for(Class<?> entityClass:entityClassSet){
        	String entityClassSimpleName = entityClass.getSimpleName();
        	if(ENTITY_CLASS_MAP.containsKey(entityClassSimpleName)){
        		throw new RuntimeException("find the same entity class: "+entityClass.getName()+" == "+ENTITY_CLASS_MAP.get(entityClassSimpleName).getName());
        	}
        	ENTITY_CLASS_MAP.put(entityClassSimpleName, entityClass);
        }
    }

    /**
     * 返回所有@Table标注的Entity类
     */
    public static Map<String, Class<?>> getEntityClassMap() {
		return ENTITY_CLASS_MAP;
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
        	entityList = QUERY_RUNNER.query(conn, sql, new ResultSetHandler<List<T>>(){
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
        	},params);
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
        	entity = QUERY_RUNNER.query(conn, sql, new ResultSetHandler<T>(){

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
        	}, params);
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
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
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
            result = QUERY_RUNNER.query(conn, sql, new MapHandler(), params);
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
            result = QUERY_RUNNER.query(conn, sql, new ScalarHandler<T>(), params);
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
            rows = QUERY_RUNNER.update(conn, sql, params);
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
        String sql = "INSERT INTO " + getTableName(entity.getClass());
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        Object[] params = new Object[entityFieldMethodList.size()];
        for (int i=0;i<entityFieldMethodList.size();i++) {
        	EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
            columns.append(column).append(", ");
            values.append("?, ");
            params[i] = ReflectionUtil.invokeMethod(entity, method);
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns + " VALUES " + values;

        return executeUpdate(sql, params);
    }

    /**
     * 更新实体
     */
    public static int updateEntity(Object entity) {
        String sql = "UPDATE " + getTableName(entity.getClass()) + " SET ";
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        //#会做修改的字段
        StringBuilder columns = new StringBuilder();
        //#修改的条件
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        //#占位符的值
        Object[] params = new Object[entityFieldMethodList.size()];
        boolean hashIdField = false;
        for (int i=0;i<entityFieldMethodList.size();i++) {
        	EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	if(!field.isAnnotationPresent(Id.class)){
        		//#没有@Id注解的字段作为修改内容
        		columns.append(column).append("=?, ");
        	}else{
        		//#有@Id的字段作为主键，用来当修改条件
        		where.append(" and "+column+"=?");
        		hashIdField = true;
        	}
        	params[i] = ReflectionUtil.invokeMethod(entity, method);
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), " ");
        sql = sql+columns.toString()+where.toString();

        if(!hashIdField){
        	//注意，updateEntity，如果Entity中没有标注@Id的字段，是不能更新的，否则会where 1=1 全表更新！
        	throw new RuntimeException("update entity failure!cannot find any field with @Id in "+entity.getClass());
        }
        
        return executeUpdate(sql, params);
    }
    
    /**
     * 更新实体
     */
    public static int insertOnDuplicateKeyEntity(Object entity) {
        String sql = "INSERT INTO " + getTableName(entity.getClass());
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        //#字段
        StringBuilder columnsInsert = new StringBuilder("(");
        StringBuilder valuesInsert = new StringBuilder(" VALUES (");
        StringBuilder columnsUpdate = new StringBuilder(" ON DUPLICATE KEY UPDATE ");
        //#占位符的值
        List<Object> params = new ArrayList<>();
        boolean hashIdField = false;
        for (int i=0;i<entityFieldMethodList.size();i++) {
        	//# insert
        	EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	
        	columnsInsert.append(column).append(", ");
        	valuesInsert.append("?, ");
        	params.add(ReflectionUtil.invokeMethod(entity, method));
        }
        columnsInsert.replace(columnsInsert.lastIndexOf(", "), columnsInsert.length(), ")");
        valuesInsert.replace(valuesInsert.lastIndexOf(", "), valuesInsert.length(), ")");
        for (int i=0;i<entityFieldMethodList.size();i++) {
        	//# update
        	EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	
        	//# update
        	if(!field.isAnnotationPresent(Id.class)){
        		//#没有@Id注解的字段作为修改内容
        		columnsUpdate.append(column).append("=?, ");
        		params.add(ReflectionUtil.invokeMethod(entity, method));
        	}else{
        		hashIdField = true;
        	}
        }
        columnsUpdate.replace(columnsUpdate.lastIndexOf(", "), columnsUpdate.length(), " ");

        sql = sql+columnsInsert.toString()+valuesInsert.toString()+columnsUpdate.toString();
        
        if(!hashIdField){
        	//注意，updateEntity，如果Entity中没有标注@Id的字段，是不能更新的，否则会where 1=1 全表更新！
        	throw new RuntimeException("update entity failure!cannot find any field with @Id in "+entity.getClass());
        }
        
        return executeUpdate(sql, params.toArray());
    }


    /**
     * 删除实体
     */
    public static int deleteEntity(Object entity) {
        String sql = "DELETE FROM " + getTableName(entity.getClass());
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        //#修改的条件
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        //#占位符的值
        //#先过滤出带有@Id的EntityFieldMethod
        List<EntityFieldMethod> idFieldList = entityFieldMethodList.stream().filter(
        		entityFieldMethod->entityFieldMethod.getField().isAnnotationPresent(Id.class)
        		).collect(Collectors.toList());
        Object[] params = new Object[idFieldList.size()];
        for (int i=0;i<idFieldList.size();i++) {
        	EntityFieldMethod entityFieldMethod = idFieldList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	//#有@Id的字段作为主键，用来当修改条件
    		where.append(" and "+column+"=?");
    		params[i] = ReflectionUtil.invokeMethod(entity, method);
        }
        sql = sql+where.toString();
        
        if(CollectionUtil.isEmpty(idFieldList)){
        	//注意，deleteEntity，如果Entity中没有标注@Id的字段，是不能删除的，否则会where 1=1 全表删除！
        	throw new RuntimeException("delete entity failure!cannot find any field with @Id in "+entity.getClass());
        }
        
        return executeUpdate(sql, params);
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getEntity(Object entity){
        String sql = "SELECT * FROM " + getTableName(entity.getClass());
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        //#修改的条件
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        //#占位符的值
        //#先过滤出带有@Id的EntityFieldMethod
        List<EntityFieldMethod> idFieldList = entityFieldMethodList.stream().filter(
        		entityFieldMethod->entityFieldMethod.getField().isAnnotationPresent(Id.class)
        		).collect(Collectors.toList());
        Object[] params = new Object[idFieldList.size()];
        for (int i=0;i<idFieldList.size();i++) {
        	EntityFieldMethod entityFieldMethod = idFieldList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	//#有@Id的字段作为主键，用来当修改条件
    		where.append(" and "+column+"=?");
    		params[i] = ReflectionUtil.invokeMethod(entity, method);
        }
        sql = sql+where.toString();
        
        if(CollectionUtil.isEmpty(idFieldList)){
        	//注意，deleteEntity，如果Entity中没有标注@Id的字段，是不能删除的，否则会where 1=1 全表删除！
        	throw new RuntimeException("delete entity failure!cannot find any field with @Id in "+entity.getClass());
        }
    	return (T)queryEntity(entity.getClass(), sql, params);
    }
    
    
    /**
     * 根据Entity获取表名
     * 如果有@Table注解，就取注解值
     * 如果没有，就取类名做表名
     */
    private static String getTableName(Class<?> entityClass) {
    	String tableName = entityClass.getSimpleName();
    	if(entityClass.isAnnotationPresent(Table.class)){
    		tableName = entityClass.getAnnotation(Table.class).value();
    	}
        return tableName;
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
    
    /**
     * 解析sql中的类信息
     */
    public static String convertSql(String sql){
    	LOGGER.debug("sql : "+sql);
    	//#根据sql匹配出Entity类
    	Map<String,Class<?>> sqlEntityClassMap = matcherEntityClassMap(sql);
    	LOGGER.debug("sqlEntityClassMap : "+JsonUtil.toJson(sqlEntityClassMap));
    	//#解析表名
    	sql = convertTableName(sql,sqlEntityClassMap);
    	//#解析字段
    	sql = convertColumnName(sql, sqlEntityClassMap);
    	return sql;
    }
    
    private static Map<String,Class<?>> matcherEntityClassMap(String sql){
    	String sqlClean = sql.replaceAll("[,><=!\\+\\-\\*/\\(\\)]", " ");
    	String[] sqlWords = sqlClean.split(" ");
    	LOGGER.debug("sqlWords : "+Arrays.toString(sqlWords));
    	
    	Map<String,Class<?>> sqlEntityClassMap = new HashMap<>();
    	for(String word:sqlWords){ 
    		if(ENTITY_CLASS_MAP.containsKey(word) && !sqlEntityClassMap.containsKey(word)){
    			sqlEntityClassMap.put(word, ENTITY_CLASS_MAP.get(word));
    		}
    	}
    	return sqlEntityClassMap;
    }
    
    
    private static String convertTableName(String sql, Map<String,Class<?>> sqlEntityClassMap){
    	//#表名可能被这些东西包围，空格本身就用来分割，所以不算在内
    	for(Map.Entry<String, Class<?>> sqlEntityClassEntry:sqlEntityClassMap.entrySet()){
    		String entityClassSimpleName = sqlEntityClassEntry.getKey();
    		Class<?> entityClass = sqlEntityClassEntry.getValue();
    		Table tableAnnotation = entityClass.getAnnotation(Table.class);
    		//#替换表名
    		//这里的表达式就需要空格了
    		String tableNameReg = "([,><=!\\+\\-\\*/\\(\\) ])"+entityClassSimpleName+"([,><=!\\+\\-\\*/\\(\\) ])";
    		Pattern p = Pattern.compile(tableNameReg);
    		Matcher m = p.matcher(sql);
    		while(m.find()){//这就可以找到表名，包括表名前后的字符，后面替换的时候，就能很方便替换了
    			String tablePre = m.group(1);
    			String tableAfter = m.group(2);
    			if("+-*()".contains(tablePre)){
    				tablePre = "\\"+tablePre;
    			}
    			if("+-*()".contains(tableAfter)){
    				tableAfter = "\\"+tableAfter;
    			}
    			tableNameReg = tablePre+entityClassSimpleName+tableAfter;
    			String tableNameAround = tablePre+tableAnnotation.value()+tableAfter;
    			sql = sql.replaceAll(tableNameReg, tableNameAround);
    		}
    	}
    	
		return sql;
    }
    
    private static String convertColumnName(String sql, Map<String,Class<?>> sqlEntityClassMap){

    	for(Map.Entry<String, Class<?>> sqlEntityClassEntry:sqlEntityClassMap.entrySet()){
    		Class<?> entityClass = sqlEntityClassEntry.getValue();
    		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entityClass);
        	//#根据get方法来解析字段名
        	for(EntityFieldMethod entityFieldMethod:entityFieldMethodList){
    			String field = entityFieldMethod.getField().getName();
    			String column = StringUtil.camelToUnderline(field);
    			//前后表达式不同
    			//前面有! 
    			//前面有(
    			//前面有.
    			//后面有)
    			String columnNameReg = "([,><=\\+\\-\\*/\\(\\. ])"+field+"([,><=!\\+\\-\\*/\\) ])";
        		Pattern p = Pattern.compile(columnNameReg);
        		Matcher m = p.matcher(sql);
        		while(m.find()){//这就可以找到表名，包括表名前后的字符，后面替换的时候，就能很方便替换了
        			String columnPre = m.group(1);
        			String columnAfter = m.group(2);
        			if("+-*(.".contains(columnPre)){
        				columnPre = "\\"+columnPre;
        			}
        			if("+-*)".contains(columnAfter)){
        				columnAfter = "\\"+columnAfter;
        			}
        			columnNameReg = columnPre+field+columnAfter;
        			String columnNameAround = columnPre+column+columnAfter;
        			sql = sql.replaceAll(columnNameReg, columnNameAround);
        		}
        	}
    	}
    	
    	return sql;
    }
    
}
