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
package org.axe.constant;

/**
 * 框架配置的相关常量
 * @author CaiDongyu on 2016/4/8.
 */
public final class ConfigConstant {
    //#axe配置文件名称
	public static final String CONFIG_FILE = "axe.properties";
    
    //#系统参数配置
	public static final String AXE_HOME = "axe.home";
	public static final String AXE_EMAIL = "axe.email";
	
    //#持久层配置
	public static final String JDBC_DRIVER = "jdbc.driver";
	public static final String JDBC_URL = "jdbc.url";
	public static final String JDBC_USERNAME = "jdbc.username";
	public static final String JDBC_PASSWORD = "jdbc.password";
	public static final String JDBC_DATASOURCE = "jdbc.datasource";
	public static final String JDBC_CONNECTION_POOL_SIZE = "jdbc.connection_pool_size";
    //这个部分是jdbc配置的子项，需要JDBC_DATASOURCE+dataSourceName+以下名称配置{
	public static final String JDBC_AUTO_CREATE_TABLE = "auto_create_table";
	public static final String JDBC_SHOW_SQL = "show_sql";
	public static final String JDBC_CHARACTER = "character";
	public static final String JDBC_COLLATE = "collate";
    //}
    
    //#项目基本配置
	public static final String APP_BASE_PACKAGE = "app.base_package";
	public static final String APP_JSP_PATH = "app.jsp_path";
	public static final String APP_ASSET_PATH = "app.asset_path";
	public static final String APP_UPLOAD_LIMIT = "app.upload_limit";
}
