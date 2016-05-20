package org.jw.constant;

/**
 * 框架配置的相关常量
 * Created by CaiDongYu on 2016/4/8.
 */
public interface ConfigConstant {
    String CONFIG_FILE = "jw.properties";
    //#系统参数配置

    //#持久层配置
    String JDBC_DRIVER = "jdbc.driver";
    String JDBC_URL = "jdbc.url";
    String JDBC_USERNAME = "jdbc.username";
    String JDBC_PASSWORD = "jdbc.password";
    String JDBC_DATASOURCE = "jdbc.datasource";
    String JDBC_AUTOTABLE = "jdbc.autotable";
    
    //#项目基本配置
    String APP_BASE_PACKAGE = "app.base_package";
    String APP_JSP_PATH = "app.jsp_path";
    String APP_ASSET_PATH = "app.asset_path";
    String APP_UPLOAD_LIMIT = "app.upload_limit";
}
