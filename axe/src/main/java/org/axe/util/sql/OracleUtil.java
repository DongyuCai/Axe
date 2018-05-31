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

import org.axe.annotation.persistence.ColumnDefine;
import org.axe.annotation.persistence.Comment;
import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Transient;
import org.axe.annotation.persistence.Unique;
import org.axe.bean.persistence.EntityFieldMethod;
import org.axe.bean.persistence.PageConfig;
import org.axe.bean.persistence.SqlPackage;
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
	private static Map<String, String> JAVA2ORACLE_MAP = new HashMap<>(); // #所有列出的java到oracle的类型转换
	static {
		JAVA2ORACLE_MAP.put("byte", "number(2)");
		JAVA2ORACLE_MAP.put("java.lang.Byte", "number(2)");
		JAVA2ORACLE_MAP.put("short", "number(4)");
		JAVA2ORACLE_MAP.put("java.lang.Short", "number(4)");
		JAVA2ORACLE_MAP.put("int", "number(9)");
		JAVA2ORACLE_MAP.put("java.lang.Integer", "number(9)");
		JAVA2ORACLE_MAP.put("long", "number(18)");
		JAVA2ORACLE_MAP.put("java.lang.Long", "number(18)");
		JAVA2ORACLE_MAP.put("float", "float");
		JAVA2ORACLE_MAP.put("java.lang.Float", "number(9)");
		JAVA2ORACLE_MAP.put("double", "double");
		JAVA2ORACLE_MAP.put("java.lang.Double", "number(18)");
		JAVA2ORACLE_MAP.put("char", "char(1)");
		JAVA2ORACLE_MAP.put("java.lang.Character", "char(1)");
		JAVA2ORACLE_MAP.put("boolean", "number(1)");
		JAVA2ORACLE_MAP.put("java.lang.Boolean", "number(1)");
		JAVA2ORACLE_MAP.put("java.lang.String", "varchar(255)");
		JAVA2ORACLE_MAP.put("java.math.BigDecimal", "number");
		JAVA2ORACLE_MAP.put("java.sql.Date", "date");
		JAVA2ORACLE_MAP.put("java.sql.Timestamp", "date");
		JAVA2ORACLE_MAP.put("java.util.Date", "date");
		// byte[]
		JAVA2ORACLE_MAP.put("[B", "blob");
	}

	public static List<String> getTableCreateSql(String dataSourceName, Class<?> entityClass) {
		String tableName = TableHelper.getTableName(entityClass);
		List<String> sqlList = new ArrayList<>();
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
			createTableSqlBufer.append("").append(column).append("");
			String columnDefine = javaType2OracleColumnDefine(field, true);
			if (StringUtil.isEmpty(columnDefine)) {
				throw new RuntimeException(entityClass.getName() + "#[" + field.getName() + "] connot convert to oracle type from " + field.getType().getName());
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
				createTableSqlBufer.append("").append(column).append("");
				String columnDefine = javaType2OracleColumnDefine(primaryKeyField, false);
				if (StringUtil.isEmpty(columnDefine)) {
					throw new RuntimeException(entityClass.getName() + "#[" + primaryKeyField.getName()
							+ "] connot convert to oracle type from " + primaryKeyField.getType().getName());
				}
				createTableSqlBufer.append(" ").append(columnDefine).append(",");
			}

			createTableSqlBufer.append("CONSTRAINT ").append(tableName).append("_pk").append(" PRIMARY KEY (");
			for (int i = 0; i < primaryKeyFieldList.size(); i++) {
				Field primaryKeyField = primaryKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append("").append(column).append("");
				if (i < primaryKeyFieldList.size() - 1) {
					createTableSqlBufer.append(",");
				}
			}
			createTableSqlBufer.append(")");

		}

		// #唯一键约束
		if (CollectionUtil.isNotEmpty(uniqueKeyFieldList)) {
			createTableSqlBufer.append(",");

			createTableSqlBufer.append("CONSTRAINT ").append(tableName).append("_uq").append("UNIQUE KEY (");
			for (int i = 0; i < uniqueKeyFieldList.size(); i++) {
				Field primaryKeyField = uniqueKeyFieldList.get(i);
				String column = StringUtil.camelToUnderline(primaryKeyField.getName());
				createTableSqlBufer.append("").append(column).append("");
				if (i < uniqueKeyFieldList.size() - 1) {
					createTableSqlBufer.append(",");
				}
			}
			createTableSqlBufer.append(")");
		}

		createTableSqlBufer.append(")");
		sqlList.add(createTableSqlBufer.toString());
		createTableSqlBufer.delete(0, createTableSqlBufer.length());

		// 注解
		for (Field field : primaryKeyFieldList) {
			if (field.isAnnotationPresent(Comment.class)) {
				String column = StringUtil.camelToUnderline(field.getName());
				createTableSqlBufer.append("COMMENT ON COLUMN ").append(tableName).append(".").append(column)
						.append(" IS '").append(field.getAnnotation(Comment.class).value()).append("'");
				sqlList.add(createTableSqlBufer.toString());
				createTableSqlBufer.delete(0, createTableSqlBufer.length());
			}
		}
		for (Field field : normalKeyFieldList) {
			if (field.isAnnotationPresent(Comment.class)) {
				String column = StringUtil.camelToUnderline(field.getName());
				createTableSqlBufer.append("COMMENT ON COLUMN ").append(tableName).append(".").append(column)
						.append(" IS '").append(field.getAnnotation(Comment.class).value()).append("'");
				sqlList.add(createTableSqlBufer.toString());
				createTableSqlBufer.delete(0, createTableSqlBufer.length());
			}
		}

		// 主键如果自增，则要创建sequence
		if (primaryKeyFieldList.size() == 1) {
			// #若只有一个@Id主键，那么默认 AUTO_INCREMENT
			Field field = primaryKeyFieldList.get(0);
			if (!field.isAnnotationPresent(ColumnDefine.class)) {
				if (field.getAnnotation(Id.class).idGenerateWay().equals(IdGenerateWay.AUTO_INCREMENT)) {
					createTableSqlBufer.append("CREATE SEQUENCE ").append(tableName).append("_sq").append(" ")
							.append("MINVALUE 1 ").append("MAXVALUE 99999999 ").append("START WITH 1 ")
							.append("INCREMENT BY 1 ").append("CACHE 20");
					sqlList.add(createTableSqlBufer.toString());
					createTableSqlBufer.delete(0, createTableSqlBufer.length());
				}
			}
		}
		return sqlList;
	}

	private static String javaType2OracleColumnDefine(Field field, boolean nullAble) {
		StringBuilder columnDefine = new StringBuilder();
		if (field.isAnnotationPresent(ColumnDefine.class)) {
			columnDefine.append(field.getAnnotation(ColumnDefine.class).value());
		} else {
			String javaType = field.getType().getName();
			String dbColumnType = JAVA2ORACLE_MAP.get(javaType);
			if (StringUtil.isNotEmpty(dbColumnType)) {
				columnDefine.append(nullAble ? dbColumnType + " DEFAULT NULL" : dbColumnType + " NOT NULL");
			}
		}
		return columnDefine.toString();
	}

	public static SqlPackage getInsertSqlPackage(Object entity) {
		String tableName = TableHelper.getTableName(entity.getClass());
		String sql = "INSERT INTO " + tableName;
		List<EntityFieldMethod> entityFieldMethodList = ReflectionUtil.getGetMethodList(entity.getClass());
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");
		List<Object> params = new ArrayList<>();
		for (EntityFieldMethod entityFieldMethod: entityFieldMethodList) {
			Field field = entityFieldMethod.getField();
			if (field.isAnnotationPresent(Transient.class)) {
				if (!field.getAnnotation(Transient.class).save()) {
					continue;
				}
			}
			
			//如果有@Id，如果是AutoIncrement，那么就自增
			//并且对应的值是空的，才自增
			Method method = entityFieldMethod.getMethod();
			Object param = ReflectionUtil.invokeMethod(entity, method);
			if (field.isAnnotationPresent(Id.class) && 
				field.getAnnotation(Id.class).idGenerateWay().equals(IdGenerateWay.AUTO_INCREMENT) && 
				param == null
				) {
				String column = StringUtil.camelToUnderline(field.getName());
				columns.append(column).append(", ");
				values.append(tableName+"_sq.nextval, ");
			}else{
				String column = StringUtil.camelToUnderline(field.getName());
				columns.append(column).append(", ");
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
		String tableName = TableHelper.getTableName(entity.getClass());
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
			params = newParams;
		} while (false);
		return new SqlPackage(sql, params, paramTypes, new boolean[] { getFlagComm, getFlagSpec });
	}

}
