package org.axe.helper.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Table;
import org.axe.annotation.persistence.Transient;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.bean.persistence.PageConfig;
import org.axe.bean.persistence.SqlPackage;
import org.axe.helper.base.ConfigHelper;
import org.axe.util.CastUtil;
import org.axe.util.CollectionUtil;
import org.axe.util.JsonUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sql 解析 助手类
 * 剥离自DataBaseHelper
 * Created by CaiDongYu on 2016/5/6.
 */
public final class SqlHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlHelper.class);

	public static Map<String,Class<?>> matcherEntityClassMap(String sql){
		Map<String, Class<?>> entityClassMap = TableHelper.getEntityClassMap();
    	String sqlClean = sql.replaceAll("[,><=!\\+\\-\\*/\\(\\)]", " ");
    	String[] sqlWords = sqlClean.split(" ");
    	LOGGER.debug("sqlWords : "+Arrays.toString(sqlWords));
    	
    	Map<String,Class<?>> sqlEntityClassMap = new HashMap<>();
    	for(String word:sqlWords){ 
    		if(entityClassMap.containsKey(word) && !sqlEntityClassMap.containsKey(word)){
    			sqlEntityClassMap.put(word, entityClassMap.get(word));
    		}
    	}
    	return sqlEntityClassMap;
    }
	
	public static SqlPackage getInsertSqlPackage(Object entity){
		String sql = "INSERT INTO " + TableHelper.getTableName(entity.getClass());
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        Object[] params = new Object[entityFieldMethodList.size()];
        for (int i=0;i<entityFieldMethodList.size();i++) {
        	EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
        	Field field = entityFieldMethod.getField();
			if(field.isAnnotationPresent(Transient.class)){
				if(!field.getAnnotation(Transient.class).save()){
					continue;
				}
			}
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
            columns.append("`").append(column).append("`, ");
            values.append("?, ");
            params[i] = ReflectionUtil.invokeMethod(entity, method);
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns + " VALUES " + values;
        return new SqlPackage(sql, params, null);
	}
	
	public static SqlPackage getUpdateSqlPackage(Object entity){
		String sql = "UPDATE " + TableHelper.getTableName(entity.getClass()) + " SET ";
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
        	if(field.isAnnotationPresent(Transient.class)){
				if(!field.getAnnotation(Transient.class).save()){
					continue;
				}
			}
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	if(!field.isAnnotationPresent(Id.class)){
        		//#没有@Id注解的字段作为修改内容
        		columns.append("`").append(column).append("`=?, ");
        	}else{
        		//#有@Id的字段作为主键，用来当修改条件
        		where.append(" and `").append(column).append("`=?");
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
        return new SqlPackage(sql, params, null);
	}
	
	
	public static SqlPackage getInsertOnDuplicateKeyUpdateSqlPackage(Object entity){
		String sql = "INSERT INTO " + TableHelper.getTableName(entity.getClass());
		//#只取拥有get方法的字段作为数据库映射字段，没有get方法的字段，认为是不需要持久化的字段
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
        	if(field.isAnnotationPresent(Transient.class)){
				if(!field.getAnnotation(Transient.class).save()){
					continue;
				}
			}
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	
        	columnsInsert.append("`").append(column).append("`, ");
        	valuesInsert.append("?, ");
        	params.add(ReflectionUtil.invokeMethod(entity, method));
        }
        columnsInsert.replace(columnsInsert.lastIndexOf(", "), columnsInsert.length(), ")");
        valuesInsert.replace(valuesInsert.lastIndexOf(", "), valuesInsert.length(), ")");
        for (int i=0;i<entityFieldMethodList.size();i++) {
        	//# update
        	EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
        	Field field = entityFieldMethod.getField();
        	if(field.isAnnotationPresent(Transient.class)){
				if(!field.getAnnotation(Transient.class).save()){
					continue;
				}
			}
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	
        	//# update
        	if(!field.isAnnotationPresent(Id.class)){
        		//#没有@Id注解的字段作为修改内容
        		columnsUpdate.append("`").append(column).append("`").append("=?, ");
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
        return new SqlPackage(sql, params.toArray(), null);
	}
	
	public static SqlPackage getDeleteSqlPackage(Object entity){
		String sql = "DELETE FROM " + TableHelper.getTableName(entity.getClass());
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        //#修改的条件
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        //#占位符的值
        //#先过滤出带有@Id的EntityFieldMethod
        List<EntityFieldMethod> idFieldList = new ArrayList<>();
        for(EntityFieldMethod entityFieldMethod : entityFieldMethodList){
        	if(entityFieldMethod.getField().isAnnotationPresent(Id.class)){
        		idFieldList.add(entityFieldMethod);
        	}
        }
        
        Object[] params = new Object[idFieldList.size()];
        for (int i=0;i<idFieldList.size();i++) {
        	EntityFieldMethod entityFieldMethod = idFieldList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	//#有@Id的字段作为主键，用来当修改条件
    		where.append(" and `").append(column).append("`=?");
    		params[i] = ReflectionUtil.invokeMethod(entity, method);
        }
        sql = sql+where.toString();
        
        if(CollectionUtil.isEmpty(idFieldList)){
        	//注意，deleteEntity，如果Entity中没有标注@Id的字段，是不能删除的，否则会where 1=1 全表删除！
        	throw new RuntimeException("delete entity failure!cannot find any field with @Id in "+entity.getClass());
        }
        return new SqlPackage(sql, params, null);
	}
	
	public static SqlPackage getSelectByIdSqlPackage(Object entity){
		String sql = "SELECT * FROM " + TableHelper.getTableName(entity.getClass());
        List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
        //#修改的条件
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        //#占位符的值
        //#先过滤出带有@Id的EntityFieldMethod
        List<EntityFieldMethod> idFieldList = new ArrayList<>();
        for(EntityFieldMethod entityFieldMethod:entityFieldMethodList){
        	if(entityFieldMethod.getField().isAnnotationPresent(Id.class)){
        		idFieldList.add(entityFieldMethod);
        	}
        }
        //注意，如果Entity中没有标注@Id的字段，就不能匹配了
        if(CollectionUtil.isEmpty(idFieldList)){
        	throw new RuntimeException("select entity failure!cannot find any field with @Id in "+entity.getClass());
        }
        

        Object[] params = new Object[idFieldList.size()];
        for (int i=0;i<idFieldList.size();i++) {
        	EntityFieldMethod entityFieldMethod = idFieldList.get(i);
        	Field field = entityFieldMethod.getField();
        	Method method = entityFieldMethod.getMethod();
        	String column = StringUtil.camelToUnderline(field.getName());
        	//#有@Id的字段作为主键，用来当修改条件
    		where.append(" and `").append(column).append("`=?");
    		params[i] = ReflectionUtil.invokeMethod(entity, method);
        }
        sql = sql+where.toString();
        
        return new SqlPackage(sql, params, null);
	}
	
	
	/**
	 * 检测sql是否可做hql转换
	 * 目前只做类到表的表名、字段是否会影响sql语句中的mysql关键字。
	 * 比如表名叫cOunt，这就会影响，因为经过驼峰转换下划线后会替换成c_ount，
	 * 会导致后续的hql解析时，如果sql中正好有count关键字，也会被替换掉，
	 * 因为在sql中cOunt不区分大小写情况下，是可以执行的，这就破坏了sql。
	 * 如果检测通过，会额外返回检测时已经匹配好的get方法和字段的map
	 */
	private static Map<String,Class<?>> checkHqlConvertIfOk(String sql){
		//匹配出Entity-Class
    	Map<String,Class<?>> sqlEntityClassMap = matcherEntityClassMap(sql);
		do{
			if(CollectionUtil.isEmpty(sqlEntityClassMap)) break;
			
			for(Map.Entry<String, Class<?>> entity:sqlEntityClassMap.entrySet()){
				String entityClassName = entity.getKey();
				//#检测表名是否影响
				//* 如果MYSQL_KEYWORDS中包含此表名，并且表名通过驼峰->下划线转换后，与原来不一致，那就不行
				if(TableHelper.checkIsMysqlKeyword(entityClassName)){
					String afterTryCast2TableName = StringUtil.camelToUnderline(entityClassName);
					if(!entityClassName.equalsIgnoreCase(afterTryCast2TableName))
						throw new RuntimeException("invalid class name["+entityClassName+"] for sql#["+sql+"], because mysql keyword will be affected!");
				}
				
				
				Class<?> entityClass = entity.getValue();
				//#检测表字锻是否影响
	    		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entityClass);
	    		if(CollectionUtil.isNotEmpty(entityFieldMethodList)){
	    			for(EntityFieldMethod efm:entityFieldMethodList){
	    				String fieldName = efm.getField().getName();
	    				//* 如果MYSQL_KEYWORDS中包含此字段名，并且表名通过驼峰->下划线转换后，与原来不一致，那就不行
	    				if(TableHelper.checkIsMysqlKeyword(fieldName)){
	    					String afterTryCast2ColumnName = StringUtil.camelToUnderline(fieldName);
	    					if(!fieldName.equalsIgnoreCase(afterTryCast2ColumnName))
	    						throw new RuntimeException("invalid field name#["+fieldName+"] in class#["+entityClassName+"] for sql#["+sql+"], because mysql keyword will be affected!");
	    				}
	    			}
	    		}
			}
		}while(false);
		return sqlEntityClassMap;
	}
	
	
	/**
     * 解析sql中的类信息
     */
    public static String[] convertHql2Sql(String sql){
    	//末尾多加一个空格，防止select * from table这样的bug，会找不到表名
    	sql = sql+" ";
    	LOGGER.debug("sql : "+sql);
    	//#根据sql匹配出Entity类
    	Map<String,Class<?>> sqlEntityClassMap =  checkHqlConvertIfOk(sql);
    	LOGGER.debug("sqlEntityClassMap : "+JsonUtil.toJson(sqlEntityClassMap));
    	String dataSourceName = null;
		if(!CollectionUtil.isEmpty(sqlEntityClassMap)){
			//#获取数据源名称，以第一个表entity的为准
			//#解析表名
			String[] sqlAndDataSource = convertTableName(sql,sqlEntityClassMap);
			sql = sqlAndDataSource[0];
			dataSourceName = sqlAndDataSource[1];
			//#解析字段
			sql = convertColumnName(sql, sqlEntityClassMap);
		}
    	return new String[]{sql,dataSourceName};
    }
    
    
    
    private static String[] convertTableName(String sql, Map<String,Class<?>> sqlEntityClassMap){
    	//#表名可能被这些东西包围，空格本身就用来分割，所以不算在内
    	String dataSourceName = null;
    	for(Map.Entry<String, Class<?>> sqlEntityClassEntry:sqlEntityClassMap.entrySet()){
    		String entityClassSimpleName = sqlEntityClassEntry.getKey();
    		Class<?> entityClass = sqlEntityClassEntry.getValue();
    		if(dataSourceName == null){
    			dataSourceName = TableHelper.getTableDataSourceName(entityClass);
    		}
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
    	
		return new String[]{sql,dataSourceName};
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
    
    /**
     * 分析占位符的模式
     * 1.纯prepareStatement的顺序占位符，?后面不带数字
     * 2.可以?后面带数字的制定参数位置的占位符
     */
    private static boolean[] analysisGetFlagMode(String sql){
    	boolean getFlagComm = false;//普通模式 就是?不带数字
    	boolean getFlagSpec = false;//?带数字模式
    	int getFlagIndex = sql.indexOf("?");
    	while(getFlagIndex >= 0 && getFlagIndex<sql.length()-1){
    		char c = sql.charAt(getFlagIndex+1);
    		if(c < '1' || c > '9'){
    			getFlagComm = true;
    		}else{
    			getFlagSpec = true;
    		}
    		getFlagIndex = sql.indexOf("?", getFlagIndex+1);
    	}
    	if(sql.trim().endsWith("?"))
    		getFlagComm = true;
    	
    	return new boolean[]{getFlagComm,getFlagSpec};
    }
    
    /**
     * 转换占位符 ?1
     * 转换pageConfig占位符
     */
    public static SqlPackage convertGetFlag(String sql,Object[] params, Class<?>[] paramTypes) {
    	//#转换pageConfig
    	SqlPackage sqlPackage = convertPagConfig(sql, params, paramTypes);
    	sql = sqlPackage.getSql();
    	params = sqlPackage.getParams();
    	paramTypes = sqlPackage.getParamTypes();
    	boolean[] getFlagModeAry = sqlPackage.getGetFlagModeAry();
    	
    	//#开始排布
    	//* 根据sql中?1这样的取值占位符，
    	boolean getFlagComm = getFlagModeAry[0];//普通模式 就是?不带数字
    	boolean getFlagSpec = getFlagModeAry[1];//?带数字模式
    	//不可以两种模式都并存，只能选一种，要么?都带数字，要么?都不带数字
    	if(getFlagComm && getFlagSpec)
			throw new RuntimeException("invalid sql statement with ?+number and only ?: "+sql);
    	List<Object> paramList = new ArrayList<>();
    	if(getFlagComm){
    		int getFlagIndex = sql.indexOf("?");
    		int paramIndex = 0;
    		while(getFlagIndex >= 0 && getFlagIndex<sql.length()){
        		Object param = params[paramIndex++];
        		if(param != null){
        			if(param.getClass().isArray()){
            			throw new RuntimeException("invalid sql param is arry: "+param);
            		}
            		if(List.class.isAssignableFrom(param.getClass())){
            			StringBuilder getFlagReplaceBuffer = new StringBuilder();
            			List<?> param2List = (List<?>)param;//这就是针对 in 操作的
            			if(CollectionUtil.isEmpty(param2List)){
            	    		throw new RuntimeException("invalid sql param List is null or empty: "+param);
            			}
            			//把元素都取出来，追加到新的元素列表里
            			for(Object eachParam:param2List){
            				paramList.add(eachParam);
            				//有一个元素，就有一个占位符
            				getFlagReplaceBuffer.append("?, ");
            			}
            			getFlagReplaceBuffer.replace(getFlagReplaceBuffer.lastIndexOf(", "), getFlagReplaceBuffer.length(), "");
            			String sql1 = sql.substring(0,getFlagIndex);
            			String sql2 = sql.substring(getFlagIndex+1);
            			sql = sql1+getFlagReplaceBuffer.toString()+sql2;
            			getFlagIndex = getFlagIndex+getFlagReplaceBuffer.length()-1;
            		}else{
            			paramList.add(param);
            		}
        		}else{
        			paramList.add(param);
        		}
        		
    			getFlagIndex = sql.indexOf("?", getFlagIndex+1);
        	}
    	}else if(getFlagSpec){
    		Pattern p = Pattern.compile("\\?([1-9][0-9]*)");
        	Matcher m = p.matcher(sql);
        	while(m.find()){
        		String getFlagNumber = m.group(1);
        		int paramIndex = CastUtil.castInteger(getFlagNumber)-1;
        		
        		Object param = params[paramIndex];
        		StringBuilder getFlagReplaceBuffer = new StringBuilder();
        		if(param != null){
        			if(param.getClass().isArray()){
            			throw new RuntimeException("invalid sql["+sql+"] param is arry: "+param);
            		}
            		
            		if(List.class.isAssignableFrom(param.getClass())){
            			List<?> param2List = (List<?>)param;//这就是针对 in 操作的
            			if(CollectionUtil.isEmpty(param2List)){
            	    		throw new RuntimeException("invalid sql param is null or empty: "+param);
            			}
            			//把元素都取出来，追加到新的元素列表里
            			for(Object eachParam:param2List){
            				paramList.add(eachParam);
            				//有一个元素，就有一个占位符
            				getFlagReplaceBuffer.append("?, ");
            			}
            			getFlagReplaceBuffer.replace(getFlagReplaceBuffer.lastIndexOf(", "), getFlagReplaceBuffer.length(), "");
            		}else{
            			getFlagReplaceBuffer.append("?");
            			paramList.add(param);
            		}
        		}else{
        			getFlagReplaceBuffer.append("?");
        			paramList.add(param);
        		}
        		
        		//把sql中对应的?1变成?或者?,?,?
        		sql = sql.replace("?"+getFlagNumber, getFlagReplaceBuffer.toString());
        	}
    	}
    	return new SqlPackage(sql, paramList.toArray(), paramTypes);
    }
    
    /**
     * 转换 分页查询条件
     * 转换有条件，如果params里包含约定位置(末尾)的pageConfig，就转换，如果没有，就不作处理
     * 但是，如果有pageConfig但是书写方式不符合约定，会报异常
     */
    public static SqlPackage convertPagConfig(String sql,Object[] params,Class<?>[] paramTypes){
    	//#检测占位符是否都符合格式
    	//?后面跟1~9,如果两位数或者更多位,则十位开始可以0~9
    	//但是只用检测个位就好
    	boolean[] getFlagModeAry = analysisGetFlagMode(sql);
    	boolean getFlagComm = getFlagModeAry[0];//普通模式 就是?不带数字
    	boolean getFlagSpec = getFlagModeAry[1];//?带数字模式
    	
    	//不可以两种模式都并存，只能选一种，要么?都带数字，要么?都不带数字
    	if(getFlagComm && getFlagSpec)
			throw new RuntimeException("invalid sql statement with ?+number and only ?: "+sql);
    	
    	//#params检测是否包含pageConfig，包含的位置
    	do{
    		PageConfig pageConfig = getPageConfigFromParams(params, paramTypes);
    		if(pageConfig == null) break;
    		
        	if(!getFlagComm && !getFlagSpec){
        		//默认这里采用getFlagSpec模式，因为默认情况下，不指定参数位置，会破坏约定，比如分页参数必须是最后一个
        		getFlagSpec = true;
        	}
        	//替换sql
    		if(sql.toUpperCase().contains(" LIMIT ")){
    			if(getFlagComm){//?
    				sql = "select * from("+sql+") limit ?,?";
    			} else if(getFlagSpec){//?1
    				sql = "select * from("+sql+") limit ?"+(paramTypes.length)+",?"+(paramTypes.length+1);
    			}
    		}else{
    			if(getFlagComm){//?
    				sql = sql+" limit ?,?";
    			} else if(getFlagSpec){//?1
    				sql = sql+" limit ?"+(paramTypes.length)+",?"+(paramTypes.length+1);
    			}
    		}
    		//替换params
    		Object[] newParams = new Object[params.length+1];
    		for(int i=0;i<newParams.length-2;i++){
    			newParams[i] = params[i];
    		}
    		newParams[newParams.length-2] = pageConfig.getLimitParam1();
    		newParams[newParams.length-1] = pageConfig.getLimitParam2();
    		params = newParams;
    	}while(false);
    	return new SqlPackage(sql, params, paramTypes,new boolean[]{getFlagComm,getFlagSpec});
    }
    
    
    public static PageConfig getPageConfigFromParams(Object[] params,Class<?>[] paramTypes){
    	PageConfig pageConfig = null;
    	do{
    		if(paramTypes == null || paramTypes.length <= 0) break;
    		Class<?> lastParamType = paramTypes[paramTypes.length-1];
    		
    		if(!PageConfig.class.isAssignableFrom(lastParamType)) break;
    		//#认为有分页要求
    		pageConfig  = (PageConfig)params[params.length-1];
    		if(pageConfig == null)
    			throw new RuntimeException("invalid sql param value of type PageConfig is null");
    	}while(false);
    	return pageConfig;
    }
    
    /**
     * 对拼接指令的转换
     * TODO：指令解析独立成解析模块
     * TODO：没有完全验证过，只小范围测试可行，稳定性待确定。
     * @return
     */
    public static String convertSqlAppendCommand(String sql,Object[] params){
		Pattern p = Pattern.compile("#([1-9][0-9]*)");
    	Matcher m = p.matcher(sql);
    	while(m.find()){
    		String getFlagNumber = m.group(1);
    		String command = "#"+getFlagNumber;
    		int paramIndex = CastUtil.castInteger(getFlagNumber)-1;
    		
    		Object param = params[paramIndex];
    		if(String.class.isAssignableFrom(param.getClass())){
    			//如果参数是字符串
    			String append = (String)param;
    			sql = sql.replaceFirst(command, append);
    		}else{
    			throw new RuntimeException("invalid sql["+sql+"] ,paramType of command[#"+getFlagNumber+"] must be String.class");
    		}
    	}
    	return sql;
    }
    
    public static String convertSqlCount(String sql){
		String sqlUpperCase = sql.toUpperCase().trim();
		if(sqlUpperCase.startsWith("SELECT ") && sqlUpperCase.contains(" FROM ")){
			String str1 = "SELECT count(1)";
			String str2 = sql.substring(sqlUpperCase.indexOf(" FROM "));
			sql = str1+str2;
		}
		return sql;
    }
    
    public static void debugSql(SqlPackage sp){
    	if(ConfigHelper.getJdbcShowSql()){
    		System.out.println(sp.getSql());
    		if(sp.getParams() != null){
    			for(int i=0;i<sp.getParams().length;i++){
    				System.out.println("arg"+(i+1)+":"+sp.getParams()[i]);
    			}
    		}
    	}
    }
    
    public static void main(String[] args) {
    	/*String sql = "select * from Test where a=b #1 #3 #12 #11";
    	Object[]  params = {"***","***","333","***","***","***","***","***","***","***","***","==="};
    	sql = convertSqlAppendCommand(sql, params);
    	System.out.println(sql);*/
    	
    	String sql = "select a,b,c from table1";
    	System.out.println(convertSqlCount(sql));
	}
}
