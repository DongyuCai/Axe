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
package org.axe.util.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.axe.annotation.persistence.ColumnDefine;
import org.axe.annotation.persistence.Comment;
import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Transient;
import org.axe.annotation.persistence.Unique;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.bean.persistence.PageConfig;
import org.axe.bean.persistence.SqlPackage;
import org.axe.constant.ConfigConstant;
import org.axe.constant.IdGenerateWay;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.persistence.TableHelper;
import org.axe.util.CollectionUtil;
import org.axe.util.PropsUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;

/**
 * Sql 解析 助手类 剥离自DataBaseHelper @author CaiDongyu on 2016/5/6.
 */
public final class MySqlUtil {
	/**
	 * mysql sql 关键字
	 */
	public static final String MYSQL_KEYWORD = ",ADD,ALL,ALTER,"
			+ "ANALYZE,AND,AS,ASC,ASENSITIVE,"
			+ "BEFORE,BETWEEN,BIGINT,BINARY,"
			+ "BLOB,BOTH,BY,CALL,CASCADE,CASE,"
			+ "CHANGE,CHAR,CHARACTER,CHECK,COLLATE,"
			+ "COLUMN,CONDITION,CONNECTION,"
			+ "CONSTRAINT,CONTINUE,CONVERT,CREATE,"
			+ "CROSS,CURRENT_DATE,CURRENT_TIME,"
			+ "CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,"
			+ "DATABASE,DATABASES,DAY_HOUR,DAY_MICROSECOND,"
			+ "DAY_MINUTE,DAY_SECOND,DEC,DECIMAL,DECLARE,"
			+ "DEFAULT,DELAYED,DELETE,DESC,DESCRIBE,"
			+ "DETERMINISTIC,DISTINCT,DISTINCTROW,"
			+ "DIV,DOUBLE,DROP,DUAL,EACH,ELSE,ELSEIF,"
			+ "ENCLOSED,ESCAPED,EXISTS,EXIT,EXPLAIN,FALSE,"
			+ "FETCH,FLOAT,FLOAT4,FLOAT8,FOR,FORCE,FOREIGN,"
			+ "FROM,FULLTEXT,GOTO,GRANT,GROUP,HAVING,"
			+ "HIGH_PRIORITY,HOUR_MICROSECOND,HOUR_MINUTE,"
			+ "HOUR_SECOND,IF,IGNORE,IN,INDEX,INFILE,INNER,"
			+ "INOUT,INSENSITIVE,INSERT,INT,INT1,INT2,INT3,"
			+ "INT4,INT8,INTEGER,INTERVAL,INTO,IS,ITERATE,"
			+ "JOIN,KEY,KEYS,KILL,LABEL,LEADING,LEAVE,LEFT,"
			+ "LIKE,LIMIT,LINEAR,LINES,LOAD,LOCALTIME,LOCALTIMESTAMP,"
			+ "LOCK,LONG,LONGBLOB,LONGTEXT,LOOP,LOW_PRIORITY,"
			+ "MATCH,MEDIUMBLOB,MEDIUMINT,MEDIUMTEXT,MIDDLEINT,"
			+ "MINUTE_MICROSECOND,MINUTE_SECOND,MOD,MODIFIES,"
			+ "NATURAL,NOT,NO_WRITE_TO_BINLOG,NULL,NUMERIC,"
			+ "ON,OPTIMIZE,OPTION,OPTIONALLY,OR,ORDER,OUT,OUTER,"
			+ "OUTFILE,PRECISION,PRIMARY,PROCEDURE,PURGE,RAID0,"
			+ "RANGE,READ,READS,REAL,REFERENCES,REGEXP,RELEASE,"
			+ "RENAME,REPEAT,REPLACE,REQUIRE,RESTRICT,RETURN,"
			+ "REVOKE,RIGHT,RLIKE,SCHEMA,SCHEMAS,SECOND_MICROSECOND,"
			+ "SELECT,SENSITIVE,SEPARATOR,SET,SHOW,SMALLINT,SPATIAL,"
			+ "SPECIFIC,SQL,SQLEXCEPTION,SQLSTATE,SQLWARNING,"
			+ "SQL_BIG_RESULT,SQL_CALC_FOUND_ROWS,SQL_SMALL_RESULT,"
			+ "SSL,STARTING,STRAIGHT_JOIN,TABLE,TERMINATED,THEN,TINYBLOB,"
			+ "TINYINT,TINYTEXT,TO,TRAILING,TRIGGER,TRUE,UNDO,UNION,"
			+ "UNIQUE,UNLOCK,UNSIGNED,UPDATE,USAGE,USE,USING,UTC_DATE,"
			+ "UTC_TIME,UTC_TIMESTAMP,VALUES,VARBINARY,VARCHAR,VARCHARACTER,"
			+ "VARYING,WHEN,WHERE,WHILE,WITH,WRITE,X509,XOR,YEAR_MONTH,ZEROFILL,"
			+ "ACTION,BIT,DATE,ENUM,NO,TEXT,TIME,TIMESTAMP,";
	

	
	
	// #所有列出的java到mysql的类型转换
	private static Map<String, String> JAVA2MYSQL_MAP = new HashMap<>(); // #所有列出的java到mysql的类型转换
	static {
		JAVA2MYSQL_MAP.put("byte", "tinyint(4)");
		JAVA2MYSQL_MAP.put("java.lang.Byte", "tinyint(4)");
		JAVA2MYSQL_MAP.put("short", "smallint(6)");
		JAVA2MYSQL_MAP.put("java.lang.Short", "smallint(6)");
		JAVA2MYSQL_MAP.put("int", "int(11)");
		JAVA2MYSQL_MAP.put("java.lang.Integer", "int(11)");
		JAVA2MYSQL_MAP.put("long", "bigint(20)");
		JAVA2MYSQL_MAP.put("java.lang.Long", "bigint(20)");
		JAVA2MYSQL_MAP.put("float", "float");
		JAVA2MYSQL_MAP.put("java.lang.Float", "float");
		JAVA2MYSQL_MAP.put("double", "double");
		JAVA2MYSQL_MAP.put("java.lang.Double", "double");
		JAVA2MYSQL_MAP.put("char", "char(1)");
		JAVA2MYSQL_MAP.put("java.lang.Character", "char(1)");
		JAVA2MYSQL_MAP.put("boolean", "bit(1)");
		JAVA2MYSQL_MAP.put("java.lang.Boolean", "bit(1)");
		JAVA2MYSQL_MAP.put("java.lang.String", "varchar(255)");
		JAVA2MYSQL_MAP.put("java.math.BigDecimal", "decimal(19,2)");
		JAVA2MYSQL_MAP.put("java.sql.Date", "datetime");
		JAVA2MYSQL_MAP.put("java.util.Date", "datetime");
		// byte[]
		JAVA2MYSQL_MAP.put("[B", "tinyblob");
	}

	public static String getTableCreateSql(String dataSourceName, Class<?> entityClass) {
		String tableName = TableHelper.getTableName(entityClass);

		StringBuilder createTableSqlBufer = new StringBuilder();
		createTableSqlBufer.append("CREATE TABLE ").append(tableName).append(" (");
		// #取含有get方法的字段，作为数据库表字段，没有get方法的字段，认为不是数据库表字段
		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entityClass);
		// #转类非主键字段到数据库表字段定义
		List<Field> primaryKeyFieldList = new ArrayList<>();
		List<Field> normalKeyFieldList = new ArrayList<>();
		;
		List<Field> uniqueKeyFieldList = new ArrayList<>();
		for (int i = 0; i < entityFieldMethodList.size(); i++) {
			EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
			Field field = entityFieldMethod.getField();
			if (field.isAnnotationPresent(Transient.class)) {
				if (!field.getAnnotation(Transient.class).save()) {
					continue;
				}
			}

			if (field.isAnnotationPresent(Id.class)) {
				// #等会儿主键处理
				primaryKeyFieldList.add(field);
			} else {
				// #普通建处理
				normalKeyFieldList.add(field);
				if (field.isAnnotationPresent(Unique.class)) {
					// #唯一键
					uniqueKeyFieldList.add(field);
				}
			}
		}
		// #普通建处理
		for (int i = 0; i < normalKeyFieldList.size(); i++) {
			Field field = normalKeyFieldList.get(i);
			String column = StringUtil.camelToUnderline(field.getName());
			createTableSqlBufer.append(column);
			String columnDefine = javaType2MysqlColumnDefine(field, true);
			if (StringUtil.isEmpty(columnDefine)) {
				throw new RuntimeException(entityClass.getName() + "#[" + field.getName() + "] connot convert to mysql type from " + field.getType().getName());
			}
			createTableSqlBufer.append(" ").append(columnDefine);

			if (i < normalKeyFieldList.size() - 1) {
				createTableSqlBufer.append(",");
			}
		}
		// #主键定义
		if (CollectionUtil.isNotEmpty(primaryKeyFieldList)) {
			createTableSqlBufer.append(",");

			for (int i = 0; i < primaryKeyFieldList.size(); i++) {
				Field primaryKeyField = primaryKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append(column);
				String columnDefine = javaType2MysqlColumnDefine(primaryKeyField, false);
				if (StringUtil.isEmpty(columnDefine)) {
					throw new RuntimeException(entityClass.getName() + "#[" + primaryKeyField.getName()
							+ "] connot convert to mysql type from " + primaryKeyField.getType().getName());
				}
				createTableSqlBufer.append(" ").append(columnDefine);
				if (primaryKeyFieldList.size() == 1) {
					// #若只有一个@Id主键，那么默认 AUTO_INCREMENT
					Field field = primaryKeyFieldList.get(0);
					if (!field.isAnnotationPresent(ColumnDefine.class)) {
						if (field.getAnnotation(Id.class).idGenerateWay().equals(IdGenerateWay.AUTO_INCREMENT)) {
							createTableSqlBufer.append(" AUTO_INCREMENT");
						}
					}
				}
				createTableSqlBufer.append(",");
			}

			createTableSqlBufer.append("PRIMARY KEY (");
			for (int i = 0; i < primaryKeyFieldList.size(); i++) {
				Field primaryKeyField = primaryKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append(column);
				if (i < primaryKeyFieldList.size() - 1) {
					createTableSqlBufer.append(",");
				}
			}
			createTableSqlBufer.append(")");

		}

		// #唯一键约束
		if (CollectionUtil.isNotEmpty(uniqueKeyFieldList)) {
			createTableSqlBufer.append(",");

			String keyName = tableName + "_uq";
			createTableSqlBufer.append("UNIQUE KEY " + keyName + " (");
			for (int i = 0; i < uniqueKeyFieldList.size(); i++) {
				Field primaryKeyField = uniqueKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append(column);
				if (i < uniqueKeyFieldList.size() - 1) {
					createTableSqlBufer.append(",");
				}
			}
			createTableSqlBufer.append(")");
		}

		Properties configProps = ConfigHelper.getCONFIG_PROPS();
		String jdbcCharacter = PropsUtil.getString(configProps,ConfigConstant.JDBC_DATASOURCE + "." + dataSourceName + "." + ConfigConstant.JDBC_CHARACTER);
		createTableSqlBufer.append(") ENGINE=InnoDB DEFAULT CHARSET=").append(jdbcCharacter);
		String jdbcCollate = PropsUtil.getString(configProps,ConfigConstant.JDBC_DATASOURCE + "." + dataSourceName + "." + ConfigConstant.JDBC_COLLATE);
		
		if (StringUtil.isNotEmpty(jdbcCollate)) {
			// 如果有校验编码，那么也要
			createTableSqlBufer.append(" COLLATE=").append(jdbcCollate);
		}

		return createTableSqlBufer.toString();
	}

	private static String javaType2MysqlColumnDefine(Field field, boolean nullAble) {
		StringBuilder columnDefine = new StringBuilder();
		if (field.isAnnotationPresent(ColumnDefine.class)) {
			columnDefine.append(field.getAnnotation(ColumnDefine.class).value());
		} else {
			String javaType = field.getType().getName();
			String dbColumnType = JAVA2MYSQL_MAP.get(javaType);
			if (StringUtil.isNotEmpty(dbColumnType)) {
				columnDefine.append(nullAble ? dbColumnType + " DEFAULT NULL" : dbColumnType + " NOT NULL");
			}
		}
		if (field.isAnnotationPresent(Comment.class)) {
			columnDefine.append(" COMMENT '").append(field.getAnnotation(Comment.class).value()).append("'");
		}
		return columnDefine.toString();
	}

	public static SqlPackage getInsertSqlPackage(Object entity) {
		String sql = "INSERT INTO " + TableHelper.getTableName(entity.getClass());
		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");
		Object[] params = new Object[entityFieldMethodList.size()];
		for (int i = 0; i < entityFieldMethodList.size(); i++) {
			EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
			Field field = entityFieldMethod.getField();
			if (field.isAnnotationPresent(Transient.class)) {
				if (!field.getAnnotation(Transient.class).save()) {
					continue;
				}
			}
			Method method = entityFieldMethod.getMethod();
			String column = StringUtil.camelToUnderline(field.getName());
			columns.append(column).append(", ");
			values.append("?, ");
			params[i] = ReflectionUtil.invokeMethod(entity, method);
		}
		columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
		values.replace(values.lastIndexOf(", "), values.length(), ")");
		sql += columns + " VALUES " + values;
		return new SqlPackage(sql, params, null);
	}
	
/*
	public static SqlPackage getInsertOnDuplicateKeyUpdateSqlPackage(Object entity) {
		String sql = "INSERT INTO " + TableHelper.getTableName(entity.getClass());
		// #只取拥有get方法的字段作为数据库映射字段，没有get方法的字段，认为是不需要持久化的字段
		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
		// #字段
		StringBuilder columnsInsert = new StringBuilder("(");
		StringBuilder valuesInsert = new StringBuilder(" VALUES (");
		StringBuilder columnsUpdate = new StringBuilder(" ON DUPLICATE KEY UPDATE ");
		// #占位符的值
		List<Object> params = new ArrayList<>();
		boolean hashIdField = false;
		for (int i = 0; i < entityFieldMethodList.size(); i++) {
			// # insert
			EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
			Field field = entityFieldMethod.getField();
			if (field.isAnnotationPresent(Transient.class)) {
				if (!field.getAnnotation(Transient.class).save()) {
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
		for (int i = 0; i < entityFieldMethodList.size(); i++) {
			// # update
			EntityFieldMethod entityFieldMethod = entityFieldMethodList.get(i);
			Field field = entityFieldMethod.getField();
			if (field.isAnnotationPresent(Transient.class)) {
				if (!field.getAnnotation(Transient.class).save()) {
					continue;
				}
			}
			Method method = entityFieldMethod.getMethod();
			String column = StringUtil.camelToUnderline(field.getName());

			// # update
			if (!field.isAnnotationPresent(Id.class)) {
				// #没有@Id注解的字段作为修改内容
				columnsUpdate.append("`").append(column).append("`").append("=?, ");
				params.add(ReflectionUtil.invokeMethod(entity, method));
			} else {
				hashIdField = true;
			}
		}
		columnsUpdate.replace(columnsUpdate.lastIndexOf(", "), columnsUpdate.length(), " ");

		sql = sql + columnsInsert.toString() + valuesInsert.toString() + columnsUpdate.toString();

		if (!hashIdField) {
			// 注意，updateEntity，如果Entity中没有标注@Id的字段，是不能更新的，否则会where 1=1 全表更新！
			throw new RuntimeException("update entity failure!cannot find any field with @Id in " + entity.getClass());
		}
		return new SqlPackage(sql, params.toArray(), null);
	}
*/

	/**
	 * 转换占位符 ?1 转换pageConfig占位符
	 */
	public static SqlPackage convertGetFlag(String sql, Object[] params, Class<?>[] paramTypes) {
		// #转换pageConfig
		SqlPackage sqlPackage = convertPagConfig(sql, params, paramTypes);
		return CommonSqlUtil.convertGetFlag(sqlPackage);
	}

	/**
	 * 转换 分页查询条件 转换有条件，如果params里包含约定位置(末尾)的pageConfig，就转换，如果没有，就不作处理
	 * 但是，如果有pageConfig但是书写方式不符合约定，会报异常
	 */
	public static SqlPackage convertPagConfig(String sql, Object[] params, Class<?>[] paramTypes) {
		// #检测占位符是否都符合格式
		// ?后面跟1~9,如果两位数或者更多位,则十位开始可以0~9
		// 但是只用检测个位就好
		boolean[] getFlagModeAry = CommonSqlUtil.analysisGetFlagMode(sql);
		boolean getFlagComm = getFlagModeAry[0];// 普通模式 就是?不带数字
		boolean getFlagSpec = getFlagModeAry[1];// ?带数字模式

		// 不可以两种模式都并存，只能选一种，要么?都带数字，要么?都不带数字
		if (getFlagComm && getFlagSpec)
			throw new RuntimeException("invalid sql statement with ?+number and only ?: " + sql);

		// #params检测是否包含pageConfig，包含的位置
		do {
			PageConfig pageConfig = CommonSqlUtil.getPageConfigFromParams(params, paramTypes);
			if (pageConfig == null)
				break;

			if (!getFlagComm && !getFlagSpec) {
				// 默认这里采用getFlagSpec模式，因为默认情况下，不指定参数位置，会破坏约定，比如分页参数必须是最后一个
				getFlagSpec = true;
			}
			// 替换sql
			if (sql.toUpperCase().contains(" LIMIT ")) {
				if (getFlagComm) {// ?
					sql = "select * from(" + sql + ") limit ?,?";
				} else if (getFlagSpec) {// ?1
					sql = "select * from(" + sql + ") limit ?" + (paramTypes.length) + ",?" + (paramTypes.length + 1);
				}
			} else {
				if (getFlagComm) {// ?
					sql = sql + " limit ?,?";
				} else if (getFlagSpec) {// ?1
					sql = sql + " limit ?" + (paramTypes.length) + ",?" + (paramTypes.length + 1);
				}
			}
			// 替换params
			Object[] newParams = new Object[params.length + 1];
			for (int i = 0; i < newParams.length - 2; i++) {
				newParams[i] = params[i];
			}
			newParams[newParams.length - 2] = pageConfig.getLimitParam1();
			newParams[newParams.length - 1] = pageConfig.getLimitParam2();
			params = newParams;
		} while (false);
		return new SqlPackage(sql, params, paramTypes, new boolean[] { getFlagComm, getFlagSpec });
	}
}
