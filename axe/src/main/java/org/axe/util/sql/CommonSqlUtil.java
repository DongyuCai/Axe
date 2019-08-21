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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.axe.bean.persistence.PageConfig;
import org.axe.bean.persistence.SqlPackage;
import org.axe.bean.persistence.TableSchema;
import org.axe.bean.persistence.TableSchema.ColumnSchema;
import org.axe.constant.ConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.persistence.TableHelper;
import org.axe.util.CastUtil;
import org.axe.util.CollectionUtil;
import org.axe.util.PropsUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;

/**
 * 统一的Sql解析处理工具，从SqlHelper中迁移过来
 * @author CaiDongyu 2018/5/29
 */
public class CommonSqlUtil {
//	private static final Logger LOGGER = LoggerFactory.getLogger(CommonSqlUtil.class);

	private CommonSqlUtil() {}
	
	public static String getShardingGtTableRecordSql(TableSchema tableSchema,int shardingFlag){
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.setLength(0);
		sqlBuffer.append("INSERT INTO ")
		.append(tableSchema.getTableName())
		.append("_sharding_gt(sharding_flag,sharding_table_status,row_count) VALUES (")
		.append(shardingFlag).append(",1,0)");
		return sqlBuffer.toString();
	}
	
	public static SqlPackage getUpdateSqlPackage(Object entity) {
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("UPDATE ").append(TableHelper.getRealTableName(entity)).append(" SET ");
		List<ColumnSchema> mappingColumnList = TableHelper.getTableSchema(entity).getMappingColumnList();
		// #会做修改的字段
		StringBuilder columns = new StringBuilder();
		// #修改的条件
		StringBuilder where = new StringBuilder(" WHERE 1=1 ");
		// #占位符的值
		List<Object> paramsColumns = new ArrayList<>();
		List<Object> paramsWhere = new ArrayList<>();
		for (ColumnSchema columnSchema : mappingColumnList) {
			if (!columnSchema.getPrimary() && !columnSchema.getJustInsert()) {
				// #没有@Id注解且不是只做新增的字段作为修改内容
				columns.append(columnSchema.getColumnName()).append("=?, ");
				paramsColumns.add(ReflectionUtil.invokeMethod(entity, columnSchema.getColumnSchema().getMethod()));
			} else {
				// #有@Id的字段作为主键，用来当修改条件
				where.append(" and ").append(columnSchema.getColumnName()).append("=?");
				paramsWhere.add(ReflectionUtil.invokeMethod(entity, columnSchema.getColumnSchema().getMethod()));
			}
		}
		columns.replace(columns.lastIndexOf(", "), columns.length(), " ");
		sqlBuffer.append(columns.toString()).append(where.toString());

		if (CollectionUtil.isEmpty(paramsWhere)) {
			//注意，updateEntity，如果Entity中没有标注@Id的字段，是不能更新的，否则会where 1=1 全表更新！
			throw new RuntimeException("update entity failure!cannot find any field with @Id in " + entity.getClass());
		}
		paramsColumns.addAll(paramsWhere);
		return new SqlPackage(sqlBuffer.toString(), paramsColumns.toArray(), null);
	}

	public static SqlPackage getDeleteSqlPackage(Object entity) {
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("DELETE FROM ").append(TableHelper.getRealTableName(entity));
		List<ColumnSchema> mappingColumnList = TableHelper.getTableSchema(entity).getMappingColumnList();
		// #修改的条件
		StringBuilder where = new StringBuilder(" WHERE 1=1 ");
		// #占位符的值
		// #先过滤出带有@Id的EntityFieldMethod
		List<ColumnSchema> idColumnList = new ArrayList<>();
		for (ColumnSchema columnSchema : mappingColumnList) {
			if (columnSchema.getPrimary()) {
				idColumnList.add(columnSchema);
			}
		}

		Object[] params = new Object[idColumnList.size()];
		for (int i = 0; i < idColumnList.size(); i++) {
			ColumnSchema columnSchema = idColumnList.get(i);
			// #有@Id的字段作为主键，用来当修改条件
			where.append(" and ").append(columnSchema.getColumnName()).append("=?");
			params[i] = ReflectionUtil.invokeMethod(entity, columnSchema.getColumnSchema().getMethod());
		}
		sqlBuffer.append(where.toString());

		if (CollectionUtil.isEmpty(idColumnList)) {
			// 注意，deleteEntity，如果Entity中没有标注@Id的字段，是不能删除的，否则会where 1=1 全表删除！
			throw new RuntimeException("delete entity failure!cannot find any field with @Id in " + entity.getClass());
		}
		return new SqlPackage(sqlBuffer.toString(), params, null);
	}

	public static SqlPackage getSelectByIdSqlPackage(Object entity) {
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("SELECT * FROM ").append(TableHelper.getRealTableName(entity));
		List<ColumnSchema> mappingColumnList = TableHelper.getTableSchema(entity).getMappingColumnList();
		// #修改的条件
		StringBuilder where = new StringBuilder(" WHERE 1=1 ");
		// #占位符的值
		// #先过滤出带有@Id的EntityFieldMethod
		List<ColumnSchema> idColumnList = new ArrayList<>();
		for (ColumnSchema columnSchema:mappingColumnList) {
			if (columnSchema.getPrimary()) {
				idColumnList.add(columnSchema);
			}
		}
		// 注意，如果Entity中没有标注@Id的字段，就不能匹配了
		if (CollectionUtil.isEmpty(idColumnList)) {
			throw new RuntimeException("select entity failure!cannot find any field with @Id in " + entity.getClass());
		}

		Object[] params = new Object[idColumnList.size()];
		for (int i = 0; i < idColumnList.size(); i++) {
			ColumnSchema columnSchema = idColumnList.get(i);
			// #有@Id的字段作为主键，用来当修改条件
			where.append(" and ").append(columnSchema.getColumnName()).append("=?");
			params[i] = ReflectionUtil.invokeMethod(entity, columnSchema.getColumnSchema().getMethod());
		}
		sqlBuffer.append(where.toString());

		return new SqlPackage(sqlBuffer.toString(), params, null);
	}
	
	public static Map<String, TableSchema> matcherEntityTableMap(String sql) {
		Map<String, TableSchema> entityTableMap = TableHelper.getEntityTableMap();
		String sqlClean = sql.replaceAll("[,><=!\\+\\-\\*/\\(\\)]", " ");
		String[] sqlWords = sqlClean.split(" ");
//		LOGGER.debug("sqlWords : " + Arrays.toString(sqlWords));

		Map<String, TableSchema> sqlEntityTableMap = new HashMap<>();
		for (String word : sqlWords) {
			if (entityTableMap.containsKey(word) && !sqlEntityTableMap.containsKey(word)) {
				sqlEntityTableMap.put(word, entityTableMap.get(word));
			}
		}
		return sqlEntityTableMap;
	}

	public static boolean checkIsSqlKeyword(String SQL_KEYWORD,String word){
		return SQL_KEYWORD.contains(","+word+",");
	}
	
	/**
	 * 检测sql是否可做hql转换 目前只做到表的表名、字段是否会影响sql语句中的mysql关键字。
	 * 比如表名叫cOunt，这就会影响，因为经过驼峰转换下划线后会替换成c_ount，
	 * 会导致后续的hql解析时，如果sql中正好有count关键字，也会被替换掉，
	 * 因为在sql中cOunt不区分大小写情况下，是可以执行的，这就破坏了sql。 如果检测通过，会额外返回检测时已经匹配好的get方法和字段的map
	 */
	public static Map<String, TableSchema> convertSqlEntity2Table(String sql) {
		// 匹配出Entity-Table
		Map<String, TableSchema> sqlEntityTableMap = matcherEntityTableMap(sql);
		do {
			if (CollectionUtil.isEmpty(sqlEntityTableMap))
				break;
		} while (false);
		return sqlEntityTableMap;
	}

	/**
	 * 解析sql中的类信息
	 */
	public static List<String> convertRawSql(String sql,Map<String, TableSchema> sqlEntityTableMap,List<Map<String,String>> sqlEntityTableNameList) {
		List<String> sqlList = null;
		if(StringUtil.isNotEmpty(sql)){
			// 末尾多加一个空格，防止select * from table这样的bug，会找不到表名
			sql = sql + " ";
//			LOGGER.debug("sql : " + sql);
//			LOGGER.debug("sqlEntityClassMap : " + JsonUtil.toJson(sqlEntityClassMap));
//			LOGGER.debug("sqlEntityTableNameList : " + JsonUtil.toJson(sqlEntityTableNameList));
			if (CollectionUtil.isNotEmpty(sqlEntityTableMap)) {
				// #获取数据源名称，以第一个表entity的为准
				// #解析字段
				sql = convertColumnName(sql, sqlEntityTableMap);
			}
			if(CollectionUtil.isNotEmpty(sqlEntityTableNameList)){
				// #解析表名
				sqlList = convertTableName(sql, sqlEntityTableNameList);
			}else{
				sqlList = new ArrayList<>();
				sqlList.add(sql);
			}
		}
		return sqlList;
	}
	
	private static List<String> convertTableName(String sql, List<Map<String,String>> sqlEntityTableNameList) {
		
		List<String> sqlList = new ArrayList<>();
		sqlList.add(sql);

		// #表名可能被这些东西包围，空格本身就用来分割，所以不算在内
		for (Map<String, String> sqlEntityTableNameMap : sqlEntityTableNameList) {
			List<String> tmpSqlList = new ArrayList<>();
			for(String rawSql:sqlList){
				for(String tableName:sqlEntityTableNameMap.keySet()){
					String entityClassSimpleName = sqlEntityTableNameMap.get(tableName);
					
//					String tableName = TableHelper.getTableName(entityClass);
					// #替换表名
					// 这里的表达式就需要空格了
					String tableNameReg = "([,><=!\\+\\-\\*/\\(\\) ])" + entityClassSimpleName + "([,><=!\\+\\-\\*/\\(\\) ])";
					Pattern p = Pattern.compile(tableNameReg);
					String tmpSql = new String(rawSql);
					Matcher m = p.matcher(tmpSql);
					while (m.find()) {// 这就可以找到表名，包括表名前后的字符，后面替换的时候，就能很方便替换了
						String tablePre = m.group(1);
						String tableAfter = m.group(2);
						if ("+-*()".contains(tablePre)) {
							tablePre = "\\" + tablePre;
						}
						if ("+-*()".contains(tableAfter)) {
							tableAfter = "\\" + tableAfter;
						}
						tableNameReg = tablePre + entityClassSimpleName + tableAfter;
						String tableNameAround = tablePre + tableName + tableAfter;
						tmpSql = tmpSql.replaceAll(tableNameReg, tableNameAround);
					}
					tmpSqlList.add(tmpSql);
				}
			}
			sqlList = tmpSqlList;
		}
		
		return sqlList;
	}

	private static String convertColumnName(String sql, Map<String, TableSchema> sqlEntityTableMap) {

		for (Map.Entry<String, TableSchema> entry : sqlEntityTableMap.entrySet()) {
			TableSchema tableSchema = entry.getValue();
			List<ColumnSchema> mappingColumnList = tableSchema.getMappingColumnList();
			// #根据get方法来解析字段名
			for (ColumnSchema columnSchema : mappingColumnList) {
				// TODO:暂时先解决=前后一样字段名的问题，这个问题是因为两个相同的字段名，中间只隔了一个字符，导致下面的正则匹配，只能匹配到一侧
				sql = sql.replaceAll(columnSchema.getFieldName() + "=" + columnSchema.getFieldName(), columnSchema.getFieldName() + " = " + columnSchema.getFieldName());

				// 前后表达式不同
				// 前面有!
				// 前面有(
				// 前面有.
				// 后面有)
				String columnNameReg = "([,><=\\+\\-\\*/\\(\\. ])" + columnSchema.getFieldName() + "([,><=!\\+\\-\\*/\\) ])";
				Pattern p = Pattern.compile(columnNameReg);
				Matcher m = p.matcher(sql);
				while (m.find()) {// 这就可以找到表名，包括表名前后的字符，后面替换的时候，就能很方便替换了
					String columnPre = m.group(1);
					String columnAfter = m.group(2);
					if ("+-*(.".contains(columnPre)) {
						columnPre = "\\" + columnPre;
					}
					if ("+-*)".contains(columnAfter)) {
						columnAfter = "\\" + columnAfter;
					}
					columnNameReg = columnPre + columnSchema.getFieldName() + columnAfter;
					String columnNameAround = columnPre + columnSchema.getColumnName() + columnAfter;
					sql = sql.replaceAll(columnNameReg, columnNameAround);
				}
			}
		}

		return sql;
	}

	/**
	 * 分析占位符的模式 1.纯prepareStatement的顺序占位符，?后面不带数字 2.可以?后面带数字的制定参数位置的占位符
	 */
	public static boolean[] analysisGetFlagMode(String sql) {
		boolean getFlagComm = false;// 普通模式 就是?不带数字
		boolean getFlagSpec = false;// ?带数字模式
		int getFlagIndex = sql.indexOf("?");
		while (getFlagIndex >= 0 && getFlagIndex < sql.length() - 1) {
			char c = sql.charAt(getFlagIndex + 1);
			if (c < '1' || c > '9') {
				getFlagComm = true;
			} else {
				getFlagSpec = true;
			}
			getFlagIndex = sql.indexOf("?", getFlagIndex + 1);
		}
		if (sql.trim().endsWith("?"))
			getFlagComm = true;

		return new boolean[] { getFlagComm, getFlagSpec };
	}

	/**
	 * 转换占位符 ?1 转换pageConfig占位符
	 */
	public static SqlPackage convertGetFlag(SqlPackage sqlPackage) {
		// #转换pageConfig
		String sql = sqlPackage.getSql();
		Object[] params = sqlPackage.getParams();
		Class<?>[] paramTypes = sqlPackage.getParamTypes();
		boolean[] getFlagModeAry = sqlPackage.getGetFlagModeAry();

		// #开始排布
		// * 根据sql中?1这样的取值占位符，
		boolean getFlagComm = getFlagModeAry[0];// 普通模式 就是?不带数字
		boolean getFlagSpec = getFlagModeAry[1];// ?带数字模式
		// 不可以两种模式都并存，只能选一种，要么?都带数字，要么?都不带数字
		if (getFlagComm && getFlagSpec)
			throw new RuntimeException("invalid sql statement with ?+number and only ?: " + sql);
		List<Object> paramList = new ArrayList<>();
		if (getFlagComm) {
			int getFlagIndex = sql.indexOf("?");
			int paramIndex = 0;
			while (getFlagIndex >= 0 && getFlagIndex < sql.length()) {
				Object param = params[paramIndex++];
				if (param != null) {
					if (param.getClass().isArray()) {
						throw new RuntimeException("invalid sql param is arry: " + param);
					}
					if (List.class.isAssignableFrom(param.getClass())) {
						StringBuilder getFlagReplaceBuffer = new StringBuilder();
						List<?> param2List = (List<?>) param;// 这就是针对 in 操作的
						if (CollectionUtil.isEmpty(param2List)) {
							throw new RuntimeException("invalid sql param List is null or empty: " + param);
						}
						// 把元素都取出来，追加到新的元素列表里
						for (Object eachParam : param2List) {
							paramList.add(eachParam);
							// 有一个元素，就有一个占位符
							getFlagReplaceBuffer.append("?, ");
						}
						getFlagReplaceBuffer.replace(getFlagReplaceBuffer.lastIndexOf(", "),
								getFlagReplaceBuffer.length(), "");
						String sql1 = sql.substring(0, getFlagIndex);
						String sql2 = sql.substring(getFlagIndex + 1);
						sql = sql1 + getFlagReplaceBuffer.toString() + sql2;
						getFlagIndex = getFlagIndex + getFlagReplaceBuffer.length() - 1;
					} else {
						paramList.add(param);
					}
				} else {
					paramList.add(param);
				}

				getFlagIndex = sql.indexOf("?", getFlagIndex + 1);
			}
		} else if (getFlagSpec) {
			Pattern p = Pattern.compile("\\?([1-9][0-9]*)");
			Matcher m = p.matcher(sql);
			while (m.find()) {
				String getFlagNumber = m.group(1);
				int paramIndex = CastUtil.castInteger(getFlagNumber) - 1;

				Object param = params[paramIndex];
				StringBuilder getFlagReplaceBuffer = new StringBuilder();
				if (param != null) {
					if (param.getClass().isArray()) {
						throw new RuntimeException("invalid sql[" + sql + "] param is arry: " + param);
					}

					if (List.class.isAssignableFrom(param.getClass())) {
						List<?> param2List = (List<?>) param;// 这就是针对 in 操作的
						if (CollectionUtil.isEmpty(param2List)) {
							throw new RuntimeException("invalid sql param is null or empty: " + param);
						}
						// 把元素都取出来，追加到新的元素列表里
						for (Object eachParam : param2List) {
							paramList.add(eachParam);
							// 有一个元素，就有一个占位符
							getFlagReplaceBuffer.append("?, ");
						}
						getFlagReplaceBuffer.replace(getFlagReplaceBuffer.lastIndexOf(", "),
								getFlagReplaceBuffer.length(), "");
					} else {
						getFlagReplaceBuffer.append("?");
						paramList.add(param);
					}
				} else {
					getFlagReplaceBuffer.append("?");
					paramList.add(param);
				}

				// 把sql中对应的?1变成?或者?,?,?
				sql = sql.replace("?" + getFlagNumber, getFlagReplaceBuffer.toString());
			}
		}
		return new SqlPackage(sql, paramList.toArray(), paramTypes);
	}


	public static PageConfig getPageConfigFromParams(Object[] params, Class<?>[] paramTypes) {
		PageConfig pageConfig = null;
		do {
			if (paramTypes == null || paramTypes.length <= 0)
				break;
			Class<?> lastParamType = paramTypes[paramTypes.length - 1];

			if (!PageConfig.class.isAssignableFrom(lastParamType))
				break;
			// #认为有分页要求
			pageConfig = (PageConfig) params[params.length - 1];
			if (pageConfig == null)
				throw new RuntimeException("invalid sql param value of type PageConfig is null");
		} while (false);
		return pageConfig;
	}

	/**
	 * 对拼接指令的转换 TODO：指令解析独立成解析模块 TODO：没有完全验证过，只小范围测试可行，稳定性待确定。
	 * 
	 * @return
	 */
	public static String convertSqlAppendCommand(String sql, Object[] params) {
		if(StringUtil.isNotEmpty(sql)){
			Pattern p = Pattern.compile("#([1-9][0-9]*)");
			Matcher m = p.matcher(sql);
			while (m.find()) {
				String getFlagNumber = m.group(1);
				String command = "#" + getFlagNumber;
				int paramIndex = CastUtil.castInteger(getFlagNumber) - 1;

				Object param = params[paramIndex];
				if (String.class.isAssignableFrom(param.getClass())) {
					// 如果参数是字符串
					String append = (String) param;
					sql = sql.replaceFirst(command, append);
				} else {
					throw new RuntimeException(
							"invalid sql[" + sql + "] ,paramType of command[#" + getFlagNumber + "] must be String.class");
				}
			}
		}
		return sql;
	}

	public static String convertSqlCount(String sql) {
		String sqlUpperCase = sql.toUpperCase().trim();
		if (sqlUpperCase.startsWith("SELECT ") && sqlUpperCase.contains(" FROM ")) {
			String str1 = "SELECT count(1)";
			String str2 = sql.substring(sqlUpperCase.indexOf(" FROM "));
			sql = str1 + str2;
		}
		return sql;
	}
	
	public static void debugSql(String dataSourceName,SqlPackage sp) {
		Properties configProps = ConfigHelper.getCONFIG_PROPS();
		boolean showSql = PropsUtil.getBoolean(configProps,ConfigConstant.JDBC_DATASOURCE + "." + dataSourceName + "." + ConfigConstant.JDBC_SHOW_SQL);
		if (showSql) {
			System.out.println(sp.getSql());
			if (sp.getParams() != null) {
				for (int i = 0; i < sp.getParams().length; i++) {
					System.out.println("arg" + (i + 1) + ":" + sp.getParams()[i]);
				}
			}
		}
	}

	public static void main(String[] args) {
		/*
		 * String sql = "select * from Test where a=b #1 #3 #12 #11"; Object[]
		 * params =
		 * {"***","***","333","***","***","***","***","***","***","***","***",
		 * "==="}; sql = convertSqlAppendCommand(sql, params);
		 * System.out.println(sql);
		 */

		String sql = "select a,b,c from table1";
		System.out.println(convertSqlCount(sql));
	}
}
