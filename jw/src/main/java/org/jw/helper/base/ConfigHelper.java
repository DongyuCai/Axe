package org.jw.helper.base;

import org.jw.constant.ConfigConstant;
import org.jw.util.PropsUtil;

import java.util.Properties;

/**
 * 配置文件助手类
 * <p>
 * Created by CaiDongYu on 2016/4/8.
 */
public final class ConfigHelper {

    private static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);

    /**
     * 获取 JDBC 驱动
     */
    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER);
    }

    /**
     * 获取 JDBC URL
     */
    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_URL);
    }

    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME);
    }
    
    /**
     * 获取 JDBC 是否自动同步表结构
     */
    public static String getJdbcAutotable() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_AUTOTABLE);
    }

    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD);
    }
    
    /**
     * 获取 JDBC 数据源
     */
    public static String getJdbcDatasource() {
    	//默认使用jw提供的dbcp数据源
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DATASOURCE, "org.jw.factory.persistence.DbcpDataSourceFactory");
    }

    /**
     * 获取应用基础包路径
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE,null);
    }

    /**
     * 获取应用 JSP 路径（建议 /WEB-INF/view/ ）
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, null);
    }

    /**
     * 获取应用 静态资源 路径（建议 /asset/）
     */
    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSET_PATH, null);
    }

    /**
     * 获取应用 文件上传限制 单位M
     */
    public static int getAppUploadLimit() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.APP_UPLOAD_LIMIT, 0);
    }
}
