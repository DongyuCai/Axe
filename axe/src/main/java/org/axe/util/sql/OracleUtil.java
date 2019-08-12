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

import org.axe.annotation.persistence.ColumnDefine;
import org.axe.annotation.persistence.Id;
import org.axe.bean.persistence.PageConfig;
import org.axe.bean.persistence.SqlPackage;
import org.axe.bean.persistence.TableSchema;
import org.axe.bean.persistence.TableSchema.ColumnSchema;
import org.axe.constant.IdGenerateWay;
import org.axe.helper.persistence.TableHelper;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;

/**
 * Sql 解析 助手类 剥离自DataBaseHelper @author CaiDongyu on 2016/5/6.
 */
public final class OracleUtil {
	
	private OracleUtil() {}
	
	/**
	 * oracle sql 关键字
	 */
	public static final String ORACLE_KEYWORD = ",ACCESS,ADD,ALL,ALTER,AND,ANY,AS,ASC,"
			+ "AUDIT,BETWEEN,BY,CHAR,CHECK,CLUSTER,"
			+ "COLUMN,COMMENT,COMPRESS,CONNECT,CREATE,"
			+ "CURRENT,DATE,DECIMAL,DEFAULT,DELETE,"
			+ "DESC,DISTINCT,DROP,ELSE,EXCLUSIVE,EXISTS,"
			+ "FILE,FLOAT,FOR,FROM,GRANT,GROUP,HAVING,"
			+ "IDENTIFIED,IMMEDIATE,IN,INCREMENT,INDEX,"
			+ "INITIAL,INSERT,INTEGER,INTERSECT,INTO,IS,"
			+ "LEVEL,LIKE,LOCK,LONG,MAXEXTENTS,MINUS,MLSLABEL,"
			+ "MODE,MODIFY,NOAUDIT,NOCOMPRESS,NOT,NOWAIT,NULL,"
			+ "NUMBER,OF,OFFLINE,ON,ONLINE,OPTION,OR,ORDER,P,"
			+ "CTFREE,PRIOR,PRIVILEGES,PUBLIC,RAW,RENAME,"
			+ "RESOURCE,REVOKE,ROW,ROWID,ROWNUM,ROWS,SELECT,"
			+ "SESSION,SET,SHARE,SIZE,SMALLINT,START,SUCCESSFUL,"
			+ "SYNONYM,SYSDATE,TABLE,THEN,TO,TRIGGER,UID,UNION,"
			+ "UNIQUE,UPDATE,USER,VALIDATE,VALUES,VARCHAR,VARCHAR2,"
			+ "VIEW,WHENEVER,WHERE,WITH,";
	
	// #所有列出的java到oracle的类型转换
	private static Map<String, String> JAVA_TYPE_2_ORACLE_TYPE_MAP = new HashMap<>(); // #所有列出的java到oracle的类型转换
	static {
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("byte", "number(2)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Byte", "number(2)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("short", "number(4)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Short", "number(4)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("int", "number(9)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Integer", "number(9)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("long", "number(18)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Long", "number(18)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("float", "float");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Float", "number(9)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("double", "double");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Double", "number(18)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("char", "char(1)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Character", "char(1)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("boolean", "number(1)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.Boolean", "number(1)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.lang.String", "varchar(255)");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.math.BigDecimal", "number");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.sql.Date", "date");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.sql.Timestamp", "date");
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("java.util.Date", "date");
		// byte[]
		JAVA_TYPE_2_ORACLE_TYPE_MAP.put("[B", "blob");
	}

	//只是给SchemaHelper用，在框架启动初期，还没有entity对象，只能有entityClass的时候，用于统一建表
	public static List<String> getTableCreateSql(String dataSourceName, TableSchema tableSchema) {
		return getTableCreateSql(dataSourceName, tableSchema.getTableName(), tableSchema);
	}
	//给DaoAspect在做分片检测、动态建表时候用，此时已经有entity了
	public static List<String> getTableCreateSql(String dataSourceName, Object entity) throws Exception {
		return getTableCreateSql(dataSourceName, TableHelper.getRealTableName(entity), TableHelper.getTableSchema(entity));
	}
	
	private static List<String> getTableCreateSql(String dataSourceName,String tableName, TableSchema tableSchema) {
		List<String> sqlList = new ArrayList<>();
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("CREATE TABLE ").append(tableName).append(" (");
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
			sqlBuffer.append("").append(columnSchema.getColumnName()).append("");
			String columnDefine = javaType2OracleColumnDefine(columnSchema.getColumnSchema().getField(), true);
			if (StringUtil.isEmpty(columnDefine)) {
				throw new RuntimeException(tableSchema.getEntityClass().getName() + "#[" + columnSchema.getFieldName() + "] connot convert to oracle type from " + columnSchema.getFieldType());
			}
			sqlBuffer.append(" ").append(columnDefine);

			if (i < normalColumnList.size() - 1) {
				sqlBuffer.append(",");
			}
		}
		// #主键定义
		if (CollectionUtil.isNotEmpty(primaryColumnList)) {
			sqlBuffer.append(",");

			for (int i = 0; i < primaryColumnList.size(); i++) {
				ColumnSchema columnSchema = primaryColumnList.get(i);
				sqlBuffer.append("").append(columnSchema.getColumnName()).append("");
				String columnDefine = javaType2OracleColumnDefine(columnSchema.getColumnSchema().getField(), false);
				if (StringUtil.isEmpty(columnDefine)) {
					throw new RuntimeException(tableSchema.getEntityClass().getName() + "#[" + columnSchema.getFieldName()
							+ "] connot convert to oracle type from " + columnSchema.getFieldType());
				}
				sqlBuffer.append(" ").append(columnDefine).append(",");
			}

			sqlBuffer.append("CONSTRAINT ").append(tableName).append("_pk").append(" PRIMARY KEY (");
			for (int i = 0; i < primaryColumnList.size(); i++) {
				ColumnSchema columnSchema = primaryColumnList.get(i);
				sqlBuffer.append("").append(columnSchema.getColumnName()).append("");
				if (i < primaryColumnList.size() - 1) {
					sqlBuffer.append(",");
				}
			}
			sqlBuffer.append(")");

		}

		// #唯一键约束
		if (CollectionUtil.isNotEmpty(uniqueColumnList)) {
			sqlBuffer.append(",");

			sqlBuffer.append("CONSTRAINT ").append(tableName).append("_uq").append("UNIQUE KEY (");
			for (int i = 0; i < uniqueColumnList.size(); i++) {
				ColumnSchema columnSchema = uniqueColumnList.get(i);
				sqlBuffer.append("").append(columnSchema.getColumnName()).append("");
				if (i < uniqueColumnList.size() - 1) {
					sqlBuffer.append(",");
				}
			}
			sqlBuffer.append(")");
		}

		sqlBuffer.append(")");
		sqlList.add(sqlBuffer.toString());
		sqlBuffer.setLength(0);

		// 注解
		for (ColumnSchema columnSchema:primaryColumnList) {
			if (StringUtil.isNotEmpty(columnSchema.getComment())) {
				sqlBuffer.append("COMMENT ON COLUMN ").append(tableName).append(".").append(columnSchema.getColumnName())
						.append(" IS '").append(columnSchema.getComment()).append("'");
				sqlList.add(sqlBuffer.toString());
				sqlBuffer.setLength(0);
			}
		}
		for (ColumnSchema columnSchema:normalColumnList) {
			if (StringUtil.isNotEmpty(columnSchema.getComment())) {
				sqlBuffer.append("COMMENT ON COLUMN ").append(tableName).append(".").append(columnSchema.getColumnName())
						.append(" IS '").append(columnSchema.getComment()).append("'");
				sqlList.add(sqlBuffer.toString());
				sqlBuffer.setLength(0);
			}
		}

		// 主键如果自增，则要创建sequence
		if (primaryColumnList.size() == 1) {
			// #若只有一个@Id主键，那么默认 AUTO_INCREMENT
			Field field = primaryColumnList.get(0).getColumnSchema().getField();
			if (!field.isAnnotationPresent(ColumnDefine.class)) {
				if (field.getAnnotation(Id.class).idGenerateWay().equals(IdGenerateWay.AUTO_INCREMENT)) {
					sqlBuffer.append("CREATE SEQUENCE ").append(tableName).append("_sq").append(" ")
							.append("MINVALUE 1 ").append("MAXVALUE 99999999 ").append("START WITH 1 ")
							.append("INCREMENT BY 1 ").append("CACHE 20");
					sqlList.add(sqlBuffer.toString());
					sqlBuffer.setLength(0);
				}
			}
		}
		return sqlList;
	}
	/**
	 * 分片表完整结构初始化
	 * 会创建分片管理表gt表、分片数据表首张表
	 */
	public static List<String> getShardingTableCreateSql(String dataSourceName, TableSchema tableSchema) {
		List<String> sqlList = new ArrayList<>();
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("CREATE TABLE ").append(tableSchema.getTableName()).append("_sharding_gt").append(" (");
		//分片ID，来自内存计算，不要担心分布式架构或多线程并发下会出现冲突，冲突了也只是覆盖，相反自增才会出现极端情况出现两条新表分片
		sqlBuffer.append("sharding_flag number(9) PRIMARY KEY,");
		sqlBuffer.append("sharding_table_status number(9),");
		sqlBuffer.append("row_count number(18)");
		sqlBuffer.append(")");
		sqlList.add(sqlBuffer.toString());
		sqlBuffer.setLength(0);

		// 注解
		sqlBuffer.append("COMMENT ON COLUMN ").append(tableSchema.getTableName()).append("_sharding_gt").append(".sharding_flag IS '分片ID'");
		sqlList.add(sqlBuffer.toString());
		sqlBuffer.setLength(0);
		sqlBuffer.append("COMMENT ON COLUMN ").append(tableSchema.getTableName()).append("_sharding_gt").append(".sharding_table_status IS '状态：1.对应的分片数据表可以正常插入 0.不可插入'");
		sqlList.add(sqlBuffer.toString());
		sqlBuffer.setLength(0);
		sqlBuffer.append("COMMENT ON COLUMN ").append(tableSchema.getTableName()).append("_sharding_gt").append(".row_count IS '总计数据条数'");
		sqlList.add(sqlBuffer.toString());
		sqlBuffer.setLength(0);
		

		//创建分表首张数据表的sql
		sqlList.addAll(getTableCreateSql(dataSourceName,tableSchema.getTableName()+"_sharding_1",tableSchema));
		//新增分片记录
		String shardingGtTableInsertRecordSql=CommonSqlUtil.getShardingGtTableRecordSql(tableSchema, 1);
		sqlList.add(shardingGtTableInsertRecordSql);
		
		return sqlList;
	}

	private static String javaType2OracleColumnDefine(Field field, boolean nullAble) {
		StringBuilder columnDefine = new StringBuilder();
		if (field.isAnnotationPresent(ColumnDefine.class)) {
			columnDefine.append(field.getAnnotation(ColumnDefine.class).value());
		} else {
			String javaType = field.getType().getName();
			String dbColumnType = JAVA_TYPE_2_ORACLE_TYPE_MAP.get(javaType);
			if (StringUtil.isNotEmpty(dbColumnType)) {
				columnDefine.append(nullAble ? dbColumnType + " DEFAULT NULL" : dbColumnType + " NOT NULL");
			}
		}
		return columnDefine.toString();
	}

	public static SqlPackage getInsertSqlPackage(Object entity) {
		String tableName = TableHelper.getRealTableName(entity);
		String sql = "INSERT INTO " + tableName;
		List<ColumnSchema> mappingColumnList = TableHelper.getTableSchema(entity).getMappingColumnList();
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");
		List<Object> params = new ArrayList<>();
		for (ColumnSchema columnSchema: mappingColumnList) {
			//如果有@Id，如果是AutoIncrement，那么就自增
			//并且对应的值是空的，才自增
			Object param = ReflectionUtil.invokeMethod(entity, columnSchema.getColumnSchema().getMethod());
			if (columnSchema.getPrimary() && columnSchema.getPrimaryKeyAutoIncrement() && param == null
				) {
				columns.append(columnSchema.getColumnName()).append(", ");
				values.append(tableName+"_sq.nextval, ");
			}else{
				columns.append(columnSchema.getColumnName()).append(", ");
				values.append("?, ");
				params.add(param);//只有不是id，或者是id但是不是自增，或者是id也是自增，但是已经给了值，才会作为预处理参数
			}
		}
		columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
		values.replace(values.lastIndexOf(", "), values.length(), ")");
		sql += columns + " VALUES " + values;
		return new SqlPackage(sql, params.toArray(), null);
	}
	
	public static String getGenerateIdSql(Object entity){
		String tableName = TableHelper.getRealTableName(entity);
		return "select "+tableName+"_sq.currval from dual";
	}
	
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

			//改用直接拼接分页参数
			sql = sql + " limit "+pageConfig.getLimitParam1()+","+pageConfig.getLimitParam2();
			
			/* CaiDongyu 2019/2/14 因为改用直接拼接，不在动态改变参数数量和位置
			if (!getFlagComm && !getFlagSpec) {
				// 默认这里采用getFlagSpec模式，因为默认情况下，不指定参数位置，会破坏约定，比如分页参数必须是最后一个
				getFlagSpec = true;
			}
			// 替换sql
			if (getFlagComm) {// ?
				sql = "select * from(select t.*,rownum t_rn from(" + sql + ") t where rownum <= ?) where t_rn >=?";
			} else if (getFlagSpec) {// ?1
				sql = "select * from(select t.*,rownum t_rn from(" + sql + ") t where rownum <= ?" + (paramTypes.length) + ") where t_rn >=?" + (paramTypes.length + 1);
			}
			// 替换params
			Object[] newParams = new Object[params.length + 1];
			for (int i = 0; i < newParams.length - 2; i++) {
				newParams[i] = params[i];
			}
			//>=，因为rownum从1开始，所以这里要+1
			newParams[newParams.length - 1] = pageConfig.getLimitParam1()+1;
			//<=，这里本来-1，因为rownum从1开始，所以这里不用加了
			newParams[newParams.length - 2] = pageConfig.getLimitParam1()+pageConfig.getPageSize();
			params = newParams;*/
		} while (false);
		return new SqlPackage(sql, params, paramTypes, new boolean[] { getFlagComm, getFlagSpec });
	}

	public static Map<String, String> getJAVA_TYPE_2_ORACLE_TYPE_MAP() {
		return JAVA_TYPE_2_ORACLE_TYPE_MAP;
	}
}
