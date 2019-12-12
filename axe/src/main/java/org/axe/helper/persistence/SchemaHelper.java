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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.axe.bean.persistence.TableSchema;
import org.axe.constant.ConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.base.Helper;
import org.axe.util.PropsUtil;
import org.axe.util.sql.MySqlUtil;
import org.axe.util.sql.OracleUtil;

/**
 * @author CaiDongyu
 * 数据库Schema 助手类
 */
public class SchemaHelper implements Helper{
	@Override
	public void init() throws Exception {}

	@Override
	public void onStartUp() throws Exception {
		//在框架的Helper都初始化后，同步表结构，（为了支持多数据源，借鉴了Rose框架）
		Map<String, TableSchema> ENTITY_TABLE_MAP = TableHelper.getEntityTableSchemaCachedMap();
		//默认按@Table里的来
		boolean tnsBegin = false;
		for(TableSchema tableSchema:ENTITY_TABLE_MAP.values()){
			Properties configProps = ConfigHelper.getCONFIG_PROPS();
			Boolean autoCreateTable = PropsUtil.getBoolean(configProps,ConfigConstant.JDBC_DATASOURCE + "." + tableSchema.getDataSourceName() + "." + ConfigConstant.JDBC_AUTO_CREATE_TABLE,null);
			if(autoCreateTable == null){
				//默认按@Table里的来
				if(tableSchema.getAutoCreate()){
					if(!tnsBegin){
						tnsBegin = true;
						DataBaseHelper.beginTransaction();
					}
					createTable(tableSchema);
				}
			}else if(autoCreateTable){
				if(!tnsBegin){
					tnsBegin = true;
					DataBaseHelper.beginTransaction();
				}
				//全局开启，优先级最高，不管@Table如何定义，这个数据源的表全部创建
				createTable(tableSchema);
			}else{
				//全局关闭了，优先级也最高，直接不创建
			}
		}
		if(tnsBegin){
			DataBaseHelper.commitTransaction();
		}
	}
	
	public void createTable(TableSchema tableSchema) throws SQLException{
		List<String> createDataTableSqlList = new ArrayList<>();
		if(DataSourceHelper.isMySql(tableSchema.getDataSourceName())){
			if(tableSchema.getSharding()){
				//分片
				createDataTableSqlList = MySqlUtil.getShardingTableCreateSql(tableSchema.getDataSourceName(), tableSchema);
			}else{
				String tableCreateSql = MySqlUtil.getTableCreateSql(tableSchema.getDataSourceName(),tableSchema);
				createDataTableSqlList.add(tableCreateSql);
			}
		}else if(DataSourceHelper.isOracle(tableSchema.getDataSourceName())){
			if(tableSchema.getSharding()){
				createDataTableSqlList = OracleUtil.getShardingTableCreateSql(tableSchema.getDataSourceName(), tableSchema);
			}else{
				createDataTableSqlList = OracleUtil.getTableCreateSql(tableSchema.getDataSourceName(),tableSchema);
			}
			
		}else {
			throw new RuntimeException(tableSchema.getEntityClass()+" connot create table, unspported dbtype driver, only mysql/oracle");
		}
		
		String[] sqlAry = new String[createDataTableSqlList.size()];
		for(int i=0;i<sqlAry.length;i++){
			sqlAry[i] = createDataTableSqlList.get(i);
		}
		try {
			DataBaseHelper.executeUpdate(sqlAry, new Object[]{}, new Class<?>[]{}, tableSchema.getDataSourceName());
		} catch (Exception e) {
			//如果是表重复，则不做处理，这种情况
			if(e != null && e.getMessage().toUpperCase().contains("DUPLICATE")){
				//不做处理
			}else{
				throw e;
			}
		}
	}
	
}
