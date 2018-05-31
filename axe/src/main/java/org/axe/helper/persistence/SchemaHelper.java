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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.axe.constant.ConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.base.Helper;
import org.axe.util.PropsUtil;
import org.axe.util.sql.MySqlUtil;
import org.axe.util.sql.OracleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CaiDongyu
 * 数据库Schema 助手类
 */
public class SchemaHelper implements Helper{
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaHelper.class);
	
	@Override
	public void init() throws Exception {}

	@Override
	public void onStartUp() throws Exception {
		//在框架的Helper都初始化后，同步表结构，（现阶段不会开发此功能，为了支持多数据源，借鉴了Rose框架）
		Map<String, Class<?>> ENTITY_CLASS_MAP = TableHelper.getEntityClassMap();
		//默认按@Table里的来
		boolean tnsBegin = false;
		for(Class<?> entityClass:ENTITY_CLASS_MAP.values()){
			String dataSourceName = TableHelper.getTableDataSourceName(entityClass);
			Properties configProps = ConfigHelper.getCONFIG_PROPS();
			Boolean autoCreateTable = PropsUtil.getBoolean(configProps,ConfigConstant.JDBC_DATASOURCE + "." + dataSourceName + "." + ConfigConstant.JDBC_AUTO_CREATE_TABLE,null);
			try {
				if(autoCreateTable == null){
					//默认按@Table里的来
					if(TableHelper.isTableAutoCreate(entityClass)){
						if(!tnsBegin){
							tnsBegin = true;
							DataBaseHelper.beginTransaction();
						}
						createTable(entityClass);
					}
				}else if(autoCreateTable){
					if(!tnsBegin){
						tnsBegin = true;
						DataBaseHelper.beginTransaction();
					}
					//全局开启，优先级最高，不管@Table如何定义，这个数据源的表全部创建
					createTable(entityClass);
				}else{
					//全局关闭了，优先级也最高，直接不创建
				}
			} catch (Exception e) {
				LOGGER.error(entityClass+" create table failed",e);
			}
		}
		if(tnsBegin){
			DataBaseHelper.commitTransaction();
		}
	}
	
	public void createTable(Class<?> entityClass) throws SQLException{
		String dataSourceName = TableHelper.getTableDataSourceName(entityClass);
		if(DataSourceHelper.isMySql(dataSourceName)){
			String createTableSql = MySqlUtil.getTableCreateSql(dataSourceName,entityClass);
			DataBaseHelper.executeUpdate(createTableSql.toString(), new Object[]{}, new Class<?>[]{}, dataSourceName);
		}else if(DataSourceHelper.isOracle(dataSourceName)){
			List<String> createTableSqlList = OracleUtil.getTableCreateSql(dataSourceName,entityClass);
			for(String createTableSql:createTableSqlList){
				DataBaseHelper.executeUpdate(createTableSql.toString(), new Object[]{}, new Class<?>[]{}, dataSourceName);
			}
		}else {
			throw new RuntimeException(entityClass.getName()+" connot create table, unspported dbtype driver, only mysql/oracle");
		}
	}
	
}
