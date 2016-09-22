package org.axe.helper.persistence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.axe.annotation.persistence.Id;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.helper.persistence.TableHelper;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;

/**
 * @author CaiDongyu
 * 数据库Schema 助手类
 */
public class SchemaHelper {

	//#所有列出的java到mysql的类型转换
	private static Map<String,String> JAVATYPE2MYSQL_MAP = new HashMap<>();
	static{
		JAVATYPE2MYSQL_MAP.put("byte", "tinyint(4) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Byte", "tinyint(4) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("short", "smallint(6) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Short", "smallint(6) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("int", "int(11) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Integer", "int(11) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("long", "bigint(20) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Long", "bigint(20) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("float", "float DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Float", "float DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("double", "double DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Double", "double DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("char", "char(1) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Character", "char(1) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("boolean", "bit(1) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.Boolean", "bit(1) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.lang.String", "varchar(255) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.math.BigDecimal", "decimal(19,2) DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.sql.Date", "datetime DEFAULT NULL");
		JAVATYPE2MYSQL_MAP.put("java.util.Date", "date DEFAULT NULL");
		//byte[]
		JAVATYPE2MYSQL_MAP.put("[B", "tinyblob");
	}
	
	public static void createTable(Class<?> entity){
		StringBuilder createTableSqlBufer = new StringBuilder(); 
		String tableName = TableHelper.getTableName(entity);
		createTableSqlBufer.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
		//#取含有get方法的字段，作为数据库表字段，没有get方法的字段，认为不是数据库表字段
		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity);
		//#转类字段到数据库表字段定义
		List<Field> primaryKeyFieldList = new ArrayList<>();
		for(int i=0;i<entityFieldMethodList.size();i++){
			EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
			Field field = entityFieldMethod.getField();
        	String column = StringUtil.camelToUnderline(field.getName());
			createTableSqlBufer.append("`").append(column).append("`");
			String javaType = field.getType().getName();
			String columnDefine = javaType2MysqlType(javaType);
			if(StringUtil.isEmpty(columnDefine)){
				throw new RuntimeException(entity.getName()+"#["+field.getName()+"] connot convert to mysql type from "+javaType);
			}
			createTableSqlBufer.append(" ").append(columnDefine);
			
			if(i<entityFieldMethodList.size()-1){
				createTableSqlBufer.append(",");
			}
			
			if(field.isAnnotationPresent(Id.class)){
				primaryKeyFieldList.add(field);
			}
		}
		//#主键定义
		if(CollectionUtil.isNotEmpty(primaryKeyFieldList)){
			createTableSqlBufer.append(",");
			createTableSqlBufer.append("PRIMARY KEY (");
			
			for(int i=0;i<primaryKeyFieldList.size();i++){
				Field primaryKeyField = primaryKeyFieldList.get(i);
	        	String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append("`").append(column).append("`");
				if(i<primaryKeyFieldList.size()-1){
					createTableSqlBufer.append(",");
				}
			}
			createTableSqlBufer.append(")");
			
			if(primaryKeyFieldList.size() == 1){
				//#若只有一个@Id主键，那么默认 AUTO_INCREMENT
				Field idField = primaryKeyFieldList.get(0);
				String column = StringUtil.camelToUnderline(idField.getName());
	        	String javaType = idField.getType().getName();
				String columnDefine = javaType2MysqlType(javaType);
				int index = createTableSqlBufer.indexOf("`"+column+"`");
				index = index+1+column.length()+2+columnDefine.length();
				createTableSqlBufer.insert(index, " AUTO_INCREMENT");
			}
		}
		
		createTableSqlBufer.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		
		System.out.println(createTableSqlBufer.toString());
	}
	
	public static String javaType2MysqlType(String javaType){
		return JAVATYPE2MYSQL_MAP.get(javaType);
	}
}
