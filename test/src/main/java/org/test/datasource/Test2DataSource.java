package org.test.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.axe.annotation.persistence.DataSource;
import org.axe.constant.ConfigConstant;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.persistence.BaseDataSource;
import org.axe.util.PropsUtil;
import org.axe.util.StringUtil;

@DataSource("test2")
public class Test2DataSource implements BaseDataSource{
	private static Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);;

	
    //#数据库
    private final String DRIVER;
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private BasicDataSource DATA_SOURCE;
	
	public Test2DataSource() {
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
        } catch (Exception e) {
            System.out.println("jdbc driver : " + DRIVER);
            System.out.println("jdbc url : " + URL);
            System.out.println("jdbc username : " + USERNAME);
            System.out.println("jdbc password : " + PASSWORD);
            System.out.println("load jdbc driver failure");
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
		return PropsUtil.getString(CONFIG_PROPS, "jdbc.test2.url");
	}

	@Override
	public String setJdbcUserName() {
		return ConfigHelper.getJdbcUsername();
	}

	@Override
	public String setJdbcPassword() {
		return ConfigHelper.getJdbcPassword() == null?"":ConfigHelper.getJdbcPassword();
	}
}
