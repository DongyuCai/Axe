package org.axe.helper.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.persistence.Table;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;

/**
 * @Table 数据库 entity 助手类 解析表名，字段名 
 * 剥离自DataBaseHelper 
 * Created by CaiDongYu on 2016/5/6. 
 */
public final class TableHelper implements Helper{

	// #@Table 实体
	private static Map<String, Class<?>> ENTITY_CLASS_MAP;

	/**
	 * MySql 关键字
	 */
	private static final String MYSQL_KEYWORD = ",ADD,ALL,ALTER,"
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

	@Override
	public void init() {
		synchronized (this) {
			ENTITY_CLASS_MAP = new HashMap<>();
			// #加载所有@Table指定的Entity类
			// select a.* from ClassA    这句sql里的ClassA，就是entityClassMap里的key
			Set<Class<?>> entityClassSet = ClassHelper.getClassSetByAnnotation(Table.class);
			for (Class<?> entityClass : entityClassSet) {
				String entityClassSimpleName = entityClass.getSimpleName();
				if (ENTITY_CLASS_MAP.containsKey(entityClassSimpleName)) {
					throw new RuntimeException("find the same entity class: " + entityClass.getName() + " == "
							+ ENTITY_CLASS_MAP.get(entityClassSimpleName).getName());
				}
				ENTITY_CLASS_MAP.put(entityClassSimpleName, entityClass);
			}
            //TODO:启动时同步表结构，（现阶段不会开发此功能，为了支持多数据源，借鉴了Rose框架）
		}
	}

	/**
	 * 返回所有@Table标注的Entity类
	 */
	public static Map<String, Class<?>> getEntityClassMap() {
		return ENTITY_CLASS_MAP;
	}

	/**
	 * 根据Entity获取表名 如果有@Table注解，就取注解值 如果没有，就取类名做表名
	 */
	public static String getTableName(Class<?> entityClass) {
		String tableName = entityClass.getSimpleName();
		if (entityClass.isAnnotationPresent(Table.class)) {
			tableName = entityClass.getAnnotation(Table.class).value();
		}
		return tableName;
	}
	
	public static boolean checkIsMysqlKeyword(String word){
		return MYSQL_KEYWORD.contains(","+word+",");
	}

	@Override
	public void onStartUp() throws Exception {}
	
	
	/**
	 * 自动建表
	 */
	private static void autoCreateTable(){
		
	}
}
