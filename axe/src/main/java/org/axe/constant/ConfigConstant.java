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
public interface ConfigConstant {
    //#axe配置文件名称
	String CONFIG_FILE = "axe.properties";
    
    //#系统参数配置
	String AXE_HOME = "axe.home";
	String AXE_EMAIL = "axe.email";
	String AXE_SIGN_IN = "axe.signin";
	String AXE_SIGN_IN_TOKEN = "axe.signin.token";
	String AXE_CLASSHELPER_KEEP = "axe.classhelper.keep";
	
    //#持久层配置
    String JDBC_DRIVER = "jdbc.driver";
    String JDBC_URL = "jdbc.url";
    String JDBC_USERNAME = "jdbc.username";
    String JDBC_PASSWORD = "jdbc.password";
    String JDBC_DATASOURCE = "jdbc.datasource";
    String JDBC_AUTO_CREATE_TABLE = "jdbc.auto_create_table";
    String JDBC_SHOW_SQL = "jdbc.show_sql";
    String JDBC_CHARACTER = "jdbc.character";
    String JDBC_COLLATE = "jdbc.collate";
    
    //#项目基本配置
    String APP_BASE_PACKAGE = "app.base_package";
    String APP_JSP_PATH = "app.jsp_path";
    String APP_ASSET_PATH = "app.asset_path";
    String APP_UPLOAD_LIMIT = "app.upload_limit";
}
