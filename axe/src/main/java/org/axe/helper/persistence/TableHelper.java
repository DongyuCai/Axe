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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.persistence.Comment;
import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Table;
import org.axe.annotation.persistence.Unique;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.bean.persistence.TableSchema;
import org.axe.bean.persistence.TableSchema.ColumnSchema;
import org.axe.constant.IdGenerateWay;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.persistence.Sharding;
import org.axe.interface_.persistence.TableNameEditor;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;
import org.axe.util.sql.CommonSqlUtil;
import org.axe.util.sql.MySqlUtil;
import org.axe.util.sql.OracleUtil;

/**
 * @Table 数据库 entity 助手类 解析表名，字段名 
 * 剥离自DataBaseHelper 
 * @author CaiDongyu on 2016/5/6. 
 */
public final class TableHelper implements Helper{

	// #@Table 实体
	private static Map<String, TableSchema> ENTITY_TABLE_MAP;

	@Override
	public void init() throws Exception{
		synchronized (this) {
			ENTITY_TABLE_MAP = new HashMap<>();
			// #加载所有@Table指定的Entity类
			// select a.* from ClassA    这句sql里的ClassA，就是entityClassMap里的key
			Set<Class<?>> entityClassSet = ClassHelper.getClassSetByAnnotation(Table.class);
			for (Class<?> entityClass : entityClassSet) {
				String entityClassSimpleName = entityClass.getSimpleName();
				if (ENTITY_TABLE_MAP.containsKey(entityClassSimpleName)) {
					throw new Exception("find the same entity class: " + entityClass.getName() + " == "
							+ ENTITY_TABLE_MAP.get(entityClassSimpleName).getEntityClass().getName());
				}
				
				//2019/8/6 分析Table结构体{
				TableSchema tableSchema = convertEntityClass2TableSchema(entityClass);
				//}
				
				ENTITY_TABLE_MAP.put(entityClassSimpleName, tableSchema);
			}
			
		}
	}

	public static TableSchema convertEntityClass2TableSchema(Class<?> entityClass) throws Exception{
		TableSchema tableSchema = new TableSchema();
		tableSchema.setTableName(getTableName(entityClass));
		tableSchema.setTableComment(getTableComment(entityClass));
		tableSchema.setAutoCreate(isTableAutoCreate(entityClass));
		tableSchema.setSharding(Sharding.class.isAssignableFrom(entityClass));
		
		tableSchema.setDataSourceName(getTableDataSourceName(entityClass));
		tableSchema.setEntityClass(entityClass);

		// #检测表名是否影响
		String sqlKeyword = "";
		if(DataSourceHelper.isMySql(tableSchema.getDataSourceName())){
			sqlKeyword = MySqlUtil.MYSQL_KEYWORD;
		}else if(DataSourceHelper.isOracle(tableSchema.getDataSourceName())){
			sqlKeyword = OracleUtil.ORACLE_KEYWORD;
		}else{
			throw new Exception("unsported dataSource type of "+tableSchema.getDataSourceName());
		}
		
		String entityClassName = entityClass.getSimpleName();
		// * 如果KEYWORDS中包含此类名，并且类对于的表名两者不一样，那么就不行了
		if (CommonSqlUtil.checkIsSqlKeyword(sqlKeyword,entityClassName.toUpperCase())) {
			if (!entityClassName.equalsIgnoreCase(tableSchema.getTableName()))
				throw new Exception("invalid class name[" + entityClassName + "] because sql keyword will be affected!");
		}
		
		// #取含有get方法的字段，作为数据库表字段，没有get方法的字段，认为不是数据库表字段
		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entityClass);
		StringBuilder idsBuffer = new StringBuilder();
		StringBuilder uniqueBuffer = new StringBuilder();
		List<ColumnSchema> mappingColumnList = new ArrayList<>();
		if(CollectionUtil.isNotEmpty(entityFieldMethodList)){
			for(EntityFieldMethod efm:entityFieldMethodList){
				Field field = efm.getField();
				
				ColumnSchema columnSchema = new ColumnSchema();
				columnSchema.setFieldName(field.getName());
				if(field.isAnnotationPresent(Comment.class)){
					columnSchema.setComment(field.getAnnotation(Comment.class).value());
				}else{
					columnSchema.setComment("");
				}
				columnSchema.setColumnName(StringUtil.camelToUnderline(columnSchema.getFieldName()));
				
				
				columnSchema.setFieldType(field.getType().getSimpleName());
				columnSchema.setColumnSchema(efm);
				mappingColumnList.add(columnSchema);
				
				if (field.isAnnotationPresent(Id.class)) {
					columnSchema.setPrimary(true);
					// #主键
					if(idsBuffer.length() > 0){
						idsBuffer.append(",");
					}
					idsBuffer.append(columnSchema.getFieldName());
					
					if(field.getAnnotation(Id.class).idGenerateWay().equals(IdGenerateWay.AUTO_INCREMENT)){
						columnSchema.setPrimaryKeyAutoIncrement(true);//自增
					}
				} else if (field.isAnnotationPresent(Unique.class)) {
					columnSchema.setUnique(true);
					// #唯一键
					if(uniqueBuffer.length() > 0){
						uniqueBuffer.append(",");
					}
				}
				
				
				// 如果KEYWORDS中包含此字段名，并且字段名通过驼峰->下划线转换后，与原来不一致，那就不行
				if (CommonSqlUtil.checkIsSqlKeyword(sqlKeyword,columnSchema.getFieldName().toUpperCase())) {
					if (!columnSchema.getFieldName().equalsIgnoreCase(columnSchema.getColumnName()))
						throw new RuntimeException(
								"invalid field name#[" + columnSchema.getColumnName() + "] in class#[" + entityClassName
										+ "] because sql keyword will be affected!");
				}
				
			}
		}
		tableSchema.setMappingColumnList(mappingColumnList);
		
		tableSchema.setIdColumns(idsBuffer.toString());
		tableSchema.setUniqueColumns(uniqueBuffer.toString());
		
		return tableSchema;
	}
	
	/**
	 * 返回所有@Table标注的Entity类
	 */
	public static Map<String, TableSchema> getEntityTableMap() {
		return ENTITY_TABLE_MAP;
	}

	/**
	 * 返回所有@Table标注的Entity类
	 */
	public static TableSchema getTableSchema(Class<?> entityClass) {
		return ENTITY_TABLE_MAP.get(entityClass.getSimpleName());
	}

	/**
	 * 返回所有@Table标注的Entity类
	 */
	public static TableSchema getTableSchema(Object entity) {
		return getTableSchema(entity.getClass());
	}
	
	/**
	 * 根据Entity Class获取表名 必须有@Table注解
	 */
	private static String getTableName(Class<?> entityClass) {
		if (entityClass.isAnnotationPresent(Table.class)) {
			return entityClass.getAnnotation(Table.class).value();
		}else{
			throw new RuntimeException(entityClass.getName() + " is not a table entity class,no @Table annotation is found on it");
		}
	}

	/**
	 * 根据Entity实例获取表名 必须有@Table注解
	 * 这个方法兼顾了分表
	 */
	public static String getRealTableName(Object entity) {
		Class<?> entityClass = entity.getClass();
		if(TableNameEditor.class.isAssignableFrom(entityClass)){
			//如果是可修改表名的接口实现类
			try {
				TableNameEditor tne = (TableNameEditor)entity;
				return tne.realTableName();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}else{
			//如果只是普通的表实体类，就用类解析表名
			return getTableName(entityClass);
		}
	}
	

	/**
	 * 根据Entity Class获取表注释 必须有@Table注解
	 */
	public static String getTableComment(Class<?> entityClass) {
		if (entityClass.isAnnotationPresent(Table.class)) {
			String comment = entityClass.getAnnotation(Table.class).comment();
			return StringUtil.isNotEmpty(comment)?comment:getTableName(entityClass);
		}else{
			throw new RuntimeException(entityClass.getName() + " is not a table entity class,no @Table annotation is found on it");
		}
	}

    private static <T> String getTableDataSourceName(Class<T> entityClass){
    	if(entityClass != null && entityClass.isAnnotationPresent(Table.class)){
    		//1.如果注明了数据源名称，就找到相应数据源返回
    		String dataSourceName = entityClass.getAnnotation(Table.class).dataSource();
    		return dataSourceName.equals("")?DataSourceHelper.getDefaultDataSourceName():dataSourceName;
    	}else{
    		//2.如果没有注明所属数据源，返回默认数据源
    		return DataSourceHelper.getDefaultDataSourceName();
    	}
    }
	
	public static boolean isTableAutoCreate(Class<?> entityClass){
		if (entityClass.isAnnotationPresent(Table.class)) {
			return entityClass.getAnnotation(Table.class).autoCreate();
		}else{
			throw new RuntimeException(entityClass.getName() + " is not a table entity class,no @Table annotation is found on it");
		}
	}

	@Override
	public void onStartUp() throws Exception {}
}
