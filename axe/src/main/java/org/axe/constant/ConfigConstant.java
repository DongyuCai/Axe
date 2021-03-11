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
	public static final String AXE_EMAIL_NOTIFICATION = "axe.email_notification";//是否打开axe邮箱通知，如果打开，下面的配置才生效
	public static final String AXE_EMAIL_SERVER_HOST = "axe.email_server_host";//邮箱host地址
	public static final String AXE_EMAIL_SERVER_PORT = "axe.email_server_port";//邮箱host端口
	public static final String AXE_EMAIL_SERVER_USER_NAME = "axe.email_server_user_name";//邮箱host的用户名，就是邮箱地址
	public static final String AXE_EMAIL_SERVER_PASSWORD = "axe.email_server_password";//邮箱host的密码
	public static final String AXE_EMAIL_TITLE = "axe.email_title";//邮件的抬头，除了配置文件里以外，也可以在代码里运行时再修改
	public static final String AXE_EMAIL_ERROR_ADDRESSEE = "axe.email_error_addressee";//系统异常的默认邮件接受地址，多个地址可以用英文逗号分隔
	
    //#项目基本配置
	public static final String APP_BASE_PACKAGE = "app.base_package";
	public static final String APP_JSP_PATH = "app.jsp_path";
	public static final String APP_ASSET_PATH = "app.asset_path";
	public static final String APP_UPLOAD_LIMIT = "app.upload_limit";
}
