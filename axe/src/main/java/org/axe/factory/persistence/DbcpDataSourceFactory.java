package org.axe.factory.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.persistence.DataSource;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbcpDataSourceFactory implements DataSource{
	Logger LOGGER = LoggerFactory.getLogger(DbcpDataSourceFactory.class);
	
    //#数据库
    private final String DRIVER;
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private final BasicDataSource DATA_SOURCE;
	
	public DbcpDataSourceFactory() {
        //#初始化jdbc配置
        DRIVER = ConfigHelper.getJdbcDriver();
        URL = ConfigHelper.getJdbcUrl();
        USERNAME = ConfigHelper.getJdbcUsername();
        PASSWORD = ConfigHelper.getJdbcPassword();
        DATA_SOURCE = new BasicDataSource();
        
        do{
        	if(StringUtil.isEmpty(DRIVER)) break;
        	if(StringUtil.isEmpty(URL)) break;
        	if(StringUtil.isEmpty(USERNAME)) break;
        	if(StringUtil.isEmpty(PASSWORD)) break;
        	//么有配置的话，默认不会初始化数据源
        	init();
        }while(false);
	}
	
	private void init() {
        
        try {
        	DATA_SOURCE.setDriverClassName(DRIVER);
        	DATA_SOURCE.setUrl(URL);
        	DATA_SOURCE.setUsername(USERNAME);
        	DATA_SOURCE.setPassword(PASSWORD);
            //TODO:启动时同步表结构，（现阶段不会开发此功能，为了支持多数据源，借鉴了Rose框架）
        } catch (Exception e) {
            LOGGER.error("jdbc driver : " + DRIVER);
            LOGGER.error("jdbc url : " + URL);
            LOGGER.error("jdbc username : " + USERNAME);
            LOGGER.error("jdbc password : " + PASSWORD);
            LOGGER.error("load jdbc driver failure", e);
        }
        
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DATA_SOURCE.getConnection();
	}

	@Override
	public String setName() {
		return "";
	}
}
