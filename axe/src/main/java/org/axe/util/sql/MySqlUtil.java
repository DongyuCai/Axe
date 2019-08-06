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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.axe.annotation.persistence.ColumnDefine;
import org.axe.annotation.persistence.Comment;
import org.axe.bean.persistence.PageConfig;
import org.axe.bean.persistence.SqlPackage;
import org.axe.bean.persistence.TableSchema;
import org.axe.bean.persistence.TableSchema.ColumnSchema;
import org.axe.constant.ConfigConstant;
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
	
	private MySqlUtil() {}
	
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
	private static Map<String, String> JAVA_TYPE_2_MYSQL_TYPE_MAP = new HashMap<>(); // #所有列出的java到mysql的类型转换
	static {
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("byte", "tinyint(4)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Byte", "tinyint(4)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("short", "smallint(6)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Short", "smallint(6)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("int", "int(11)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Integer", "int(11)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("long", "bigint(20)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Long", "bigint(20)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("float", "float");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Float", "float");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("double", "double");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Double", "double");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("char", "char(1)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Character", "char(1)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("boolean", "bit(1)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.Boolean", "bit(1)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.lang.String", "varchar(255)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.math.BigDecimal", "decimal(19,2)");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.sql.Date", "datetime");
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("java.util.Date", "datetime");
		// byte[]
		JAVA_TYPE_2_MYSQL_TYPE_MAP.put("[B", "tinyblob");
	}

	//只是给SchemaHelper用，在框架启动初期，还没有entity对象，只能有entityClass的时候，用于统一建表
	public static String getTableCreateSql(String dataSourceName, TableSchema tableSchema) {
		return getTableCreateSql(dataSourceName, tableSchema.getTableName(), tableSchema);
	}
	//给DaoAspect在做分片检测、动态建表时候用，此时已经有entity了
	public static String getTableCreateSql(String dataSourceName, Object entity) throws Exception {
		TableSchema tableSchema = TableHelper.getTableSchema(entity);
		//由于是通过entity拿到的表结构，因此getTableName应该是真实的tableName
		tableSchema.setTableName(TableHelper.getRealTableName(entity));
		return getTableCreateSql(dataSourceName, tableSchema);
	}
	
	private static String getTableCreateSql(String dataSourceName,String tableName,TableSchema tableSchema){
		StringBuilder createTableSqlBufer = new StringBuilder();
		createTableSqlBufer.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
		
		List<ColumnSchema> mappingColumnList = tableSchema.getMappingColumnList();
		// #转类非主键字段到数据库表字段定义
		List<ColumnSchema> primaryColumnList = new ArrayList<>();
		List<ColumnSchema> normalColumnList = new ArrayList<>();
		List<ColumnSchema> uniqueColumnList = new ArrayList<>();
		for (ColumnSchema columnSchema:mappingColumnList) {
			if (columnSchema.getPrimary()) {
				// #等会儿主键处理
				primaryColumnList.add(columnSchema);
			} else {
				// #普通建处理
				normalColumnList.add(columnSchema);
				if (columnSchema.getUnique()) {
					// #唯一键
					uniqueColumnList.add(columnSchema);
				}
			}
		}
		// #普通建处理
		for (int i = 0; i < normalColumnList.size(); i++) {
			ColumnSchema columnSchema = normalColumnList.get(i);
			createTableSqlBufer.append(columnSchema.getColumnName());
			String columnDefine = javaType2MysqlColumnDefine(columnSchema.getColumnSchema().getField(), true);
			if (StringUtil.isEmpty(columnDefine)) {
				throw new RuntimeException(tableSchema.getEntityClass().getName() + "#[" + columnSchema.getFieldName() + "] connot convert to mysql type from " + columnSchema.getFieldType());
			}
			createTableSqlBufer.append(" ").append(columnDefine);

			if (i < normalColumnList.size() - 1) {
				createTableSqlBufer.append(",");
			}
		}
		// #主键定义
		if (CollectionUtil.isNotEmpty(primaryColumnList)) {
			createTableSqlBufer.append(",");

			for (int i = 0; i < primaryColumnList.size(); i++) {
				ColumnSchema columnSchema = primaryColumnList.get(i);
				createTableSqlBufer.append(columnSchema.getColumnName());
				String columnDefine = javaType2MysqlColumnDefine(columnSchema.getColumnSchema().getField(), false);
				if (StringUtil.isEmpty(columnDefine)) {
					throw new RuntimeException(tableSchema.getEntityClass().getName() + "#[" + columnSchema.getFieldName()
							+ "] connot convert to mysql type from " + columnSchema.getFieldType());
				}
				createTableSqlBufer.append(" ").append(columnDefine);
				if (!columnSchema.getColumnSchema().getField().isAnnotationPresent(ColumnDefine.class)) {
					if (columnSchema.getPrimaryKeyAutoIncrement()) {
						createTableSqlBufer.append(" AUTO_INCREMENT");
					}
				}
				createTableSqlBufer.append(",");
			}

			createTableSqlBufer.append("PRIMARY KEY (").append(tableSchema.getIdColumns()).append(")");

		}

		// #唯一键约束
		if (CollectionUtil.isNotEmpty(uniqueColumnList)) {
			createTableSqlBufer.append(",");

			createTableSqlBufer.append("UNIQUE KEY ").append(tableName).append("_uq (").append(tableSchema.getUniqueColumns()).append(")");
		}

		Properties configProps = ConfigHelper.getCONFIG_PROPS();
		String jdbcCharacter = PropsUtil.getString(configProps,ConfigConstant.JDBC_DATASOURCE + "." + dataSourceName + "." + ConfigConstant.JDBC_CHARACTER,"utf8");
		createTableSqlBufer.append(") ENGINE=InnoDB DEFAULT CHARSET=").append(jdbcCharacter);
		String jdbcCollate = PropsUtil.getString(configProps,ConfigConstant.JDBC_DATASOURCE + "." + dataSourceName + "." + ConfigConstant.JDBC_COLLATE);
		if (StringUtil.isNotEmpty(jdbcCollate)) {
			// 如果有校验编码，那么也要
			createTableSqlBufer.append(" COLLATE=").append(jdbcCollate);
		}

		return createTableSqlBufer.toString();
	}
	
	public static String getShardingTableCreateSql(String dataSourceName, TableSchema tableSchema) {

		StringBuilder createTableSqlBufer = new StringBuilder();
		createTableSqlBufer.append("CREATE TABLE IF NOT EXISTS ").append(tableSchema.getTableName()).append("_sharding_gt").append(" (");
		//分片ID，来自内存计算，不要担心分布式架构或多线程并发下会出现冲突，冲突了也只是覆盖，相反自增才会出现极端情况出现两条新表分片
		createTableSqlBufer.append("`sharding_flag` int(11) NOT NULL COMMENT '分片ID',");
		createTableSqlBufer.append("`sharding_table_status` int(11) NOT NULL COMMENT '状态：1.对应的分片数据表可以正常插入 0.不可插入',");
		createTableSqlBufer.append("`row_count` bigint(20) NOT NULL COMMENT '总计数据条数',");
		// #主键定义
		createTableSqlBufer.append("PRIMARY KEY (`sharding_flag`)");

		Properties configProps = ConfigHelper.getCONFIG_PROPS();
		String jdbcCharacter = PropsUtil.getString(configProps,ConfigConstant.JDBC_DATASOURCE + "." + dataSourceName + "." + ConfigConstant.JDBC_CHARACTER,"utf8");
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
			String dbColumnType = JAVA_TYPE_2_MYSQL_TYPE_MAP.get(javaType);
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
		String sql = "INSERT INTO " + TableHelper.getRealTableName(entity);
		List<ColumnSchema> mappingColumnList = TableHelper.getTableSchema(entity).getMappingColumnList();
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");
		List<Object> params = new ArrayList<>();
		for (ColumnSchema columnSchema:mappingColumnList) {
			columns.append("`").append(columnSchema.getColumnName()).append("`, ");
			values.append("?, ");
			params.add(ReflectionUtil.invokeMethod(entity, columnSchema.getColumnSchema().getMethod()));
		}
		columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
		values.replace(values.lastIndexOf(", "), values.length(), ")");
		sql += columns + " VALUES " + values;
		return new SqlPackage(sql, params.toArray(), null);
	}

	/**
	 * 转换占位符 ?1 转换pageConfig占位符
	 */
	public static SqlPackage convertGetFlag(String sql, Object[] params, Class<?>[] paramTypes) {
		// #转换pageConfig
		SqlPackage sqlPackage = convertPagConfig(sql, params, paramTypes,false);
		return CommonSqlUtil.convertGetFlag(sqlPackage);
	}

	/**
	 * 转换 分页查询条件 转换有条件，如果params里包含约定位置(末尾)的pageConfig，就转换，如果没有，就不作处理
	 * 但是，如果有pageConfig但是书写方式不符合约定，会报异常
	 */
	public static SqlPackage convertPagConfig(String sql, Object[] params, Class<?>[] paramTypes,boolean pageTypeIsUnion) {
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
			//改用直接拼接分页参数
			if(pageTypeIsUnion){
				//union模式的内部分页，是limit 0,pageNum*pageSize
				//TODO:这样有一个问题，大表如果非常大了，那么越往后分页，性能会越差
				sql = sql + " limit 0,"+(pageConfig.getPageNum()*pageConfig.getPageSize());
			}else{
				//非union模式的外部分页，就是limit n1,n2
				sql = sql + " limit "+pageConfig.getLimitParam1()+","+pageConfig.getLimitParam2();
			}
			/* CaiDongyu 2019/2/14 因为改用直接拼接，不在动态改变参数数量和位置
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
//			}
			// 替换params
			Object[] newParams = new Object[params.length + 1];
			for (int i = 0; i < newParams.length - 2; i++) {
				newParams[i] = params[i];
			}
			newParams[newParams.length - 2] = pageConfig.getLimitParam1();
			newParams[newParams.length - 1] = pageConfig.getLimitParam2();
			params = newParams;*/
		} while (false);
		return new SqlPackage(sql, params, paramTypes, new boolean[] { getFlagComm, getFlagSpec });
	}
	
	public static Map<String, String> getJAVA_TYPE_2_MYSQL_TYPE_MAP() {
		return JAVA_TYPE_2_MYSQL_TYPE_MAP;
	}
}
