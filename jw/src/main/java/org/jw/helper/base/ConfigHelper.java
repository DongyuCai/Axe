package org.jw.helper.base;

import java.util.Properties;

import org.jw.constant.ConfigConstant;
import org.jw.helper.Helper;
import org.jw.util.PropsUtil;

/**
 * 配置文件助手类
 * <p>
 * Created by CaiDongYu on 2016/4/8.
 */
public final class ConfigHelper implements Helper{

    private static Properties CONFIG_PROPS;

    @Override
    public void init() {
    	synchronized (this) {
    		CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);
		}
    }
    
    /**
     * 是否开启/jw的访问
     */
    public static boolean getJwHome(){
    	return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.JW_HOME, true);
    }

    /**
     * 是否释放框架初始化完成后的，ClassHelper内的classSet集合
     * 这个集合如果在实际项目中，可能会很大，因为不知道有多少资源会全部被扫描进来
     * 如果需要完全使用jw_home，可以不关闭，否则默认关闭
     */
    public static boolean getJwClassHelperKeep(){
    	return PropsUtil.getBoolean(CONFIG_PROPS, ConfigConstant.JW_CLASSHELPER_KEEP, false);
    }
    
    /**
     * 获取 JDBC 驱动
     */
    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER, null);
    }

    /**
     * 获取 JDBC URL
     */
    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_URL, null);
    }

    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME, null);
    }
    
    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD, null);
    }
    
    /**
     * 获取 JDBC 数据源
     */
    public static String getJdbcDatasource() {
    	//默认使用jw提供的dbcp数据源
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DATASOURCE, "org.jw.factory.persistence.DbcpDataSourceFactory");
    }

    /**
     * 指定框架扫描的包路径，多个路径使用“,”号分割
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * 指定jsp存放路径（建议 /WEB-INF/view/ ）
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, null);
    }

    /**
     * 指定静态文件(html、js、css、图片等)存放路径（建议 /asset/）
     */
    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSET_PATH, null);
    }

    /**
     * 文件上传限制单次文件大小，单位M
     */
    public static int getAppUploadLimit() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.APP_UPLOAD_LIMIT, 0);
    }
}
