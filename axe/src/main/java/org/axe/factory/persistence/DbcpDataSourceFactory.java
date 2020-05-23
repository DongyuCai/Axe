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
package org.axe.factory.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.axe.annotation.persistence.DataSource;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.persistence.BaseDataSource;
import org.axe.util.LogUtil;
import org.axe.util.StringUtil;

@DataSource("axe-datasource-dbcp")
public final class DbcpDataSourceFactory implements BaseDataSource{
	
    //#数据库
    private final String DRIVER;
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private BasicDataSource DATA_SOURCE;
	
	public DbcpDataSourceFactory() {
        //#初始化jdbc配置
        DRIVER = setJdbcDriver();
        URL = setJdbcUrl();
        USERNAME = setJdbcUserName();
        PASSWORD = setJdbcPassword();
        
        do{
        	if(StringUtil.isEmpty(DRIVER)) break;
        	if(StringUtil.isEmpty(URL)) break;
        	if(StringUtil.isEmpty(USERNAME)) break;
//        	if(StringUtil.isEmpty(PASSWORD)) break;
        	//么有配置的话，默认不会初始化数据源
        	init();
        }while(false);
	}
	
	private void init() {
        
        try {
            DATA_SOURCE = new BasicDataSource();
        	DATA_SOURCE.setDriverClassName(DRIVER);
        	DATA_SOURCE.setUrl(URL);
        	DATA_SOURCE.setUsername(USERNAME);
        	DATA_SOURCE.setPassword(PASSWORD);
        	
        	DATA_SOURCE.setMaxIdle(80);
        	DATA_SOURCE.setMinIdle(80);
        	DATA_SOURCE.setInitialSize(80);
        	DATA_SOURCE.setMaxWaitMillis(500);
        } catch (Exception e) {
            LogUtil.error("jdbc driver : " + DRIVER);
            LogUtil.error("jdbc url : " + URL);
            LogUtil.error("jdbc username : " + USERNAME);
            LogUtil.error("jdbc password : " + PASSWORD);
            LogUtil.error(e);
        }
        
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DATA_SOURCE.getConnection();
	}

	@Override
	public String setJdbcDriver() {
		return ConfigHelper.getJdbcDriver();
	}

	@Override
	public String setJdbcUrl() {
		return ConfigHelper.getJdbcUrl();
	}

	@Override
	public String setJdbcUserName() {
		return ConfigHelper.getJdbcUsername();
	}

	@Override
	public String setJdbcPassword() {
		return ConfigHelper.getJdbcPassword() == null?"":ConfigHelper.getJdbcPassword();
	}

	@Override
	public boolean tns() {
		return true;
	}
}
