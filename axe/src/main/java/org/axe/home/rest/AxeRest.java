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
package org.axe.home.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Controller;
import org.axe.annotation.ioc.Service;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Interceptor;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.annotation.persistence.Dao;
import org.axe.annotation.persistence.Tns;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.View;
import org.axe.constant.CharacterEncoding;
import org.axe.constant.ContentType;
import org.axe.constant.RequestMethod;
import org.axe.exception.RestException;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.base.FrameworkStatusHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.helper.mvc.ControllerHelper;
import org.axe.helper.mvc.FilterHelper;
import org.axe.helper.mvc.InterceptorHelper;
import org.axe.helper.persistence.DataSourceHelper;
import org.axe.helper.persistence.TableHelper;
import org.axe.home.interceptor.HomeInterceptor;
import org.axe.home.interceptor.SignInInterceptor;
import org.axe.home.service.HomeService;
import org.axe.interface_.mvc.Filter;
import org.axe.interface_.persistence.BaseDataSource;
import org.axe.util.ApiExportUtil;
import org.axe.util.ApiExportUtil.Level_1;
import org.axe.util.CollectionUtil;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.axe.util.TableExportUtil;
import org.axe.util.TableExportUtil.Table;

@FilterFuckOff
@Interceptor({HomeInterceptor.class, SignInInterceptor.class})
@Controller(basePath = "/axe")
public class AxeRest {
private static final Logger LOGGER = LoggerFactory.getLogger(AxeRest.class);

@Autowired
private HomeService homeService;

private void printHtml(HttpServletResponse response, String html) {
try {
response.setCharacterEncoding(CharacterEncoding.UTF_8.CHARACTER_ENCODING);
response.setContentType(ContentType.APPLICATION_HTML.CONTENT_TYPE);
PrintWriter writer = response.getWriter();
writer.write(html);
//writer.flush();
//writer.close();
} catch (Exception e) {
LOGGER.error("home error",e);
}
}

private void printFile(HttpServletResponse response, String path, ContentType contentType, String token) {
token = token == null?"":token;
BufferedReader reader = null;
try {
InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
if (in != null) {
reader = new BufferedReader(new InputStreamReader(in));
response.setCharacterEncoding(CharacterEncoding.UTF_8.CHARACTER_ENCODING);
response.setContentType(contentType.CONTENT_TYPE);
ServletOutputStream out = response.getOutputStream();
String line = reader.readLine();
while (line != null) {
line = line.replaceAll("\\{\\{token\\}\\}", token);
out.write((line+System.lineSeparator()).getBytes(CharacterEncoding.UTF_8.CHARACTER_ENCODING));
line = reader.readLine();
}
// writer.flush();
// writer.close();
}
} catch (Exception e) {
LOGGER.error("home error", e);
} finally {
try {
if (reader != null) {
reader.close();
}
} catch (Exception e) {
}
}
}

@Request(path = "/sign-in", method = RequestMethod.GET)
public void signIn(HttpServletRequest request, HttpServletResponse response,Param param) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe sign in</title>");
html.append("<style type=\"text/css\">");
html.append(".mytxt {");
html.append("color:#333;");
html.append("line-height:normal;");
html.append("font-family:\"Microsoft YaHei\",Tahoma,Verdana,SimSun;");
html.append("font-style:normal;");
html.append("font-variant:normal;");
html.append("font-size-adjust:none;");
html.append("font-stretch:normal;");
html.append("font-weight:normal;");
html.append("margin-top:0px;");
html.append("margin-bottom:0px;");
html.append("margin-left:0px;");
html.append("padding-top:4px;");
html.append("padding-right:4px;");
html.append("padding-bottom:4px;");
html.append("padding-left:4px;");
html.append("font-size:15px;");
html.append("outline-width:medium;");
html.append("outline-style:none;");
html.append("outline-color:invert;");
html.append("border-top-left-radius:3px;");
html.append("border-top-right-radius:3px;");
html.append("border-bottom-left-radius:3px;");
html.append("border-bottom-right-radius:3px;");
html.append("text-shadow:0px 1px 2px #fff;");
html.append("background-attachment:scroll;");
html.append("background-repeat:repeat-x;");
html.append("background-position-x:left;");
html.append("background-position-y:top;");
html.append("background-size:auto;");
html.append("background-origin:padding-box;");
html.append("background-clip:border-box;");
html.append("background-color:rgb(255,255,255);");
html.append("margin-right:8px;");
html.append("border-top-color:#ccc;");
html.append("border-right-color:#ccc;");
html.append("border-bottom-color:#ccc;");
html.append("border-left-color:#ccc;");
html.append("border-top-width:1px;");
html.append("border-right-width:1px;");
html.append("border-bottom-width:1px;");
html.append("border-left-width:1px;");
html.append("border-top-style:solid;");
html.append("border-right-style:solid;");
html.append("border-bottom-style:solid;");
html.append("border-left-style:solid;");
html.append("}");
html.append("");
html.append(".mytxt:focus {");
html.append("border: 1px solid #fafafa;");
html.append("-webkit-box-shadow: 0px 0px 6px #007eff;");
html.append("-moz-box-shadow: 0px 0px 5px #007eff;");
html.append("box-shadow: 0px 0px 5px #007eff;");
html.append("");
html.append("}");
html.append("");
html.append(".mybtn{");
html.append("-webkit-appearance: none;");
html.append("-webkit-rtl-ordering: logical;");
html.append("-webkit-user-select: none;");
html.append("background-color: rgb(49, 175, 100);");
html.append("background-image: none;");
html.append("border-bottom-color: rgb(47, 170, 96);");
html.append("border-bottom-left-radius: 3px;");
html.append("border-bottom-right-radius: 3px;");
html.append("border-bottom-style: solid;");
html.append("border-bottom-width: 1px;");
html.append("border-image-outset: 0px;");
html.append("border-image-repeat: stretch;");
html.append("border-image-slice: 100%;");
html.append("border-image-source: none;");
html.append("border-image-width: 1;");
html.append("border-left-color: rgb(47, 170, 96);");
html.append("border-left-style: solid;");
html.append("border-left-width: 1px;");
html.append("border-right-color: rgb(47, 170, 96);");
html.append("border-right-style: solid;");
html.append("border-right-width: 1px;");
html.append("border-top-color: rgb(47, 170, 96);");
html.append("border-top-left-radius: 3px;");
html.append("border-top-right-radius: 3px;");
html.append("border-top-style: solid;");
html.append("border-top-width: 1px;");
html.append("box-sizing: border-box;");
html.append("color: rgb(255, 255, 255);");
html.append("cursor: pointer;");
html.append("display: inline-block;");
html.append("font-family: 'Source Sans Pro', 'Helvetica Neue', Helvetica, Arial, sans-serif;");
html.append("font-size: 15px;");
html.append("font-stretch: normal;");
html.append("font-style: normal;");
html.append("font-variant: normal;");
html.append("font-weight: 500;");
html.append("height: 35px;");
html.append("letter-spacing: normal;");
html.append("line-height: 21.4285717010498px;");
html.append("margin-bottom: 0px;");
html.append("margin-left: 0px;");
html.append("margin-right: 0px;");
html.append("margin-top: 0px;");
html.append("padding-bottom: 6px;");
html.append("padding-left: 16px;");
html.append("padding-right: 16px;");
html.append("padding-top: 6px;");
html.append("text-align: center;");
html.append("text-indent: 0px;");
html.append("text-rendering: auto;");
html.append("text-shadow: none;");
html.append("text-transform: none;");
html.append("touch-action: manipulation;");
html.append("vertical-align: middle;");
html.append("white-space: nowrap;");
html.append("width: 100%;");
html.append("word-spacing: 0px;");
html.append("writing-mode: lr-tb;");
html.append("-webkit-writing-mode: horizontal-tb");
html.append("}");
html.append("</style>");
html.append("</head>");
html.append("<body>");
html.append("<table style=\"width: 100%;height: 600px\">");
html.append("<tr><td valign=\"middle\">");
html.append("<table style=\"width: 100%\">");
html.append("<tr><td align=\"center\">");
html.append("");
html.append("<table>");
html.append("<tr><td>");
html.append("<img width=\"200px\" height=\"200px\" src=\"https://github.com/DongyuCai/Axe/raw/18.5.29/axe/favicon.png\">");
html.append("</td><td>&nbsp;</td><td valign=\"top\">");
html.append("<form method=\"POST\" action=\""+contextPath+"/axe/sign-in\">");
html.append("<table>");
html.append("<tr>");
html.append("<td><b style=\"font-size: 30px;color: #8E8E8E\">欢迎登陆Axe!</b></td>");
html.append("</tr><tr><td height=\"10px\"></td></tr>");
html.append("<tr>");
html.append("<td><input name=\"id\" type=\"text\" class=\"mytxt\" value=\"ID\" onFocus=\"if(value==defaultValue){value='';this.style.color='#000'}\" onBlur=\"if(!value){value=defaultValue;this.style.color='#999'}\" style=\"color:#999999\"  /></td>");
html.append("</tr><tr><td height=\"3px\"></td></tr>");
html.append("<tr>");
html.append("<td><input name=\"password\" type=\"text\" class=\"mytxt\" value=\"密码\" onFocus=\"if(value==defaultValue){value='';this.style.color='#000';this.type='password';}\" onBlur=\"if(!value){value=defaultValue;this.style.color='#999';this.type='text';}\" style=\"color:#999999\" /></td>");
html.append("</tr><tr><td height=\"3px\"></td></tr>");
html.append("<tr>");
html.append("<td align=\"right\"><a style=\"font-size: 10px;color: #d0d0d0\" href=\""+contextPath+"/axe/forgot-password\">忘记密码?</a></td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td><input type=\"submit\" value=\"登录\" class=\"mybtn\" /></td>");
html.append("</tr>");
html.append("</table>");
html.append("</form>");
html.append("</td></tr>");
html.append("</table>");
html.append("");
html.append("</td></tr>");
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}

@Request(path = "/sign-in", method = RequestMethod.POST)
public View signIn(HttpServletRequest request, HttpServletResponse response,@RequestParam("id")String id,@RequestParam("password")String password) {
String contextPath = request.getContextPath();

boolean success = homeService.signIn(request, id, password);
if(success){
return new View("/axe?token="+ConfigHelper.getAxeSignInToken());
}

StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe sign in</title>");
html.append("<script type=\"text/javascript\">");
html.append("var number = 10;");
html.append("");
html.append("var int=self.setInterval(\"toHome()\",1000);");
html.append("");
html.append("function toHome(){");
html.append("number = number-1;");
html.append("document.getElementById(\"number\").innerHTML = number;");
html.append("if(number <= 0){");
html.append("window.clearInterval(int);");
html.append("window.location = \""+contextPath+"/axe/sign-in\";");
html.append("}");
html.append("}");
html.append("");
html.append("</script>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"center\"><span id=\"number\">10</span>秒后自动跳转<a href=\""+contextPath+"/axe/sign-in\">/axe/sign-in 重新登录</a></td></tr>");
html.append("<tr><td align=\"center\"><font size=\"28\"><b>登录失败，用户名或密码错误！</b></font></td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());

return null;
}



@Request(path = "/sign-out", method = RequestMethod.GET)
public View signOut(@RequestParam("token")String token, HttpServletResponse response) {
homeService.signOut();
return new View("/axe/sign-in");
}
@Request(path = "", method = RequestMethod.GET)
public void home(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe homepage</title>");
html.append("<script type=\"text/javascript\">");
html.append("var refreshInt = setInterval(\"refresh()\",1000*60);");
html.append("function refresh(){");
html.append("window.location = \""+contextPath+"/axe?token="+token+"\";");
html.append("}");
html.append("</script>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
html.append("<tr><td align=\"center\"><font size=\"28\">欢迎使用 Axe!</font></td></tr>");
html.append("");
html.append("<!--系统运行 概览-->");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>系统运行</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\"><b>启动时间</b></td>");
html.append("<td align=\"left\"><b>运行时长</b></td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
Date startupTime = FrameworkStatusHelper.getStartupTime();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
html.append("<td align=\"left\">"+sdf.format(startupTime)+"</td>");
long runTimeSec = (System.currentTimeMillis()-startupTime.getTime()) / 1000;
String runTime = "";
if (runTimeSec < 60) {
runTime = runTimeSec + "秒";
} else {
long runTimeMin = runTimeSec / 60;
if (runTimeMin < 60) {
runTimeSec = runTimeSec - (runTimeMin * 60);
runTime = runTimeMin + "分" + runTimeSec + "秒";
} else {
long runTimeHour = runTimeMin / 60;
if (runTimeHour < 24) {
runTimeMin = runTimeMin - runTimeHour * 60;
runTimeSec = runTimeSec - ((runTimeHour * 60) + runTimeMin) * 60;
runTime = runTimeHour + "时" + runTimeMin + "分" + runTimeSec + "秒";
} else {
long runTimeDay = runTimeHour / 24;
runTimeHour = runTimeHour-(runTimeDay * 24);
runTimeMin = runTimeMin - ((runTimeDay * 24) + runTimeHour) * 60;
runTimeSec = runTimeSec - ((((runTimeDay * 24) + runTimeHour) * 60) + runTimeMin) * 60;
runTime = runTimeDay + "天" + runTimeHour + "时" + runTimeMin + "分" + runTimeSec + "秒";
}
}
}
html.append("<td align=\"left\">"+runTime+"</td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("");
html.append("<!--系统参数-->");
html.append("<!-- <tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>系统参数 axe.properties</b></font>&nbsp;");
html.append("</td><td>&nbsp;</td><td style=\"background-color: #007500;cursor: pointer;\" onclick=\"window.location='"+contextPath+"/axe/axe.properties?token="+token+"'\">");
html.append("&nbsp;<font color=\"white\"><b>编辑</b></font>&nbsp;");
html.append("</td><td>&nbsp;</td><td style=\"background-color: #007500;cursor: pointer;\" onclick=\"window.location='"+contextPath+"/axe/refresh-config?token="+token+"'\">");
html.append("&nbsp;<font color=\"white\"><b>重载 axe.properties 配置</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\"><b>参数-键</b></td>");
html.append("<td align=\"left\"><b>参数-值</b></td>");
html.append("<td align=\"left\"><b>参数描述</b></td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">axe.home</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAxeHome()+"</td>");
html.append("<td align=\"left\">是否开启/axe的访问</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">axe.email</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAxeEmail()+"</td>");
html.append("<td align=\"left\">系统异常、密码找回邮件通知地址</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">axe.signin</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAxeSignIn()+"</td>");
html.append("<td align=\"left\">是否开启/axe的登录访问</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">axe.classhelper.keep</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAxeClassHelperKeep()+"</td>");
html.append("<td align=\"left\">启动后是否释放ClassHelper的内存(释放后ClassHelper不可再用)</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.driver</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcDriver()+"</td>");
html.append("<td align=\"left\">jdbc-driver</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.url</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcUrl()+"</td>");
html.append("<td align=\"left\">jdbc-url</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.username</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcUsername()+"</td>");
html.append("<td align=\"left\">jdbc-username</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.password</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcPassword()+"</td>");
html.append("<td align=\"left\">jdbc-password</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.datasource</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcDatasource()+"</td>");
html.append("<td align=\"left\">指定DataSource数据源实现类</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">app.base_package</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppBasePackage()+"</td>");
html.append("<td align=\"left\">指定框架扫描的包路径</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">app.jsp_path</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppJspPath()+"</td>");
html.append("<td align=\"left\">指定jsp存放路径</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">app.asset_path</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppAssetPath()+"</td>");
html.append("<td align=\"left\">指定静态文件(html、js、css、图片等)存放路径</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\">app.upload_limit</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppUploadLimit()+"</td>");
html.append("<td align=\"left\">文件上传限制单次文件大小，单位M，默认0不限制</td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr> -->");
html.append("");
html.append("<!--MVC 概览-->");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>MVC</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/filter?token="+token+"\">Filter</a> x"+FilterHelper.getSortedFilterList().size()+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/interceptor?token="+token+"\">Interceptor</a> x"+InterceptorHelper.getInterceptorMap().size()+"</td>");
String controllerSize = "?";
String serviceSize = "?";
String tnsPointCount = "?";
String daoSize = "?";
if(ConfigHelper.getAxeClassHelperKeep()){
controllerSize = ClassHelper.getControllerClassSet().size()+"";
Set<Class<?>> serviceClassSet = ClassHelper.getServiceClassSet();
serviceSize = serviceClassSet.size()+"";
int count = 0;
for(Class<?> serviceClass:serviceClassSet){
List<Method> methods = ReflectionUtil.getMethodByAnnotation(serviceClass, Tns.class);
if(CollectionUtil.isNotEmpty(methods)){
count = count+methods.size();
}
}
tnsPointCount = count+"";
daoSize = ClassHelper.getClassSetByAnnotation(Dao.class).size()+"";
}
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/controller?token="+token+"\">Controller</a> x"+controllerSize+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/action?token="+token+"\">Action</a> x"+ControllerHelper.getActionList().size()+"</td>");
html.append("<td align=\"left\">Service x"+serviceSize+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/tns?token="+token+"\">Tns point </a> x"+tnsPointCount+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/dao?token="+token+"\">Dao</a> x"+daoSize+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/table?s=index.html&token="+token+"\" target=\"blank\">Table</a> x"+TableHelper.getEntityTableMap().size()+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/dataSource?token="+token+"\">DataSource</a> x"+DataSourceHelper.getDataSourceAll().size()+"</td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("");
html.append("<!--工具面板 -->");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>工具面板</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr>");
html.append("<td align=\"left\" width=\"20px\">&nbsp;</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/api_test?s=index.html&token="+token+"\" target=\"blank\">Api Test</a></td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/filter", method = RequestMethod.GET)
public void filter(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe filter</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
List<Filter> filterList = FilterHelper.getSortedFilterList();
html.append("<tr><td align=\"center\"><font size=\"28\">Filter list x"+filterList.size()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Filter</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Level</b></td>");
html.append("<td align=\"left\"><b>Class</b></td>");
html.append("<td align=\"left\"><b>Mapping</b></td>");
html.append("<td align=\"left\"><b>NotMapping</b></td>");
html.append("</tr>");
int id=1;
for(Filter filter:filterList){
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+filter.setLevel()+"</td>");
html.append("<td align=\"left\">"+filter.getClass()+"</td>");
Pattern mappingPattern = filter.setMapping();
String mappingRegex = mappingPattern == null?"":mappingPattern.toString();
html.append("<td align=\"left\">"+mappingRegex+"</td>");
Pattern notMappingPattern = filter.setNotMapping();
String notMappingRegex = notMappingPattern==null?"":notMappingPattern.toString();
html.append("<td align=\"left\">"+notMappingRegex+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/interceptor", method = RequestMethod.GET)
public void interceptor(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe interceptor</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
Map<Class<? extends org.axe.interface_.mvc.Interceptor>,org.axe.interface_.mvc.Interceptor> interceptorMap = InterceptorHelper.getInterceptorMap();
html.append("<tr><td align=\"center\"><font size=\"28\">Interceptor list x"+interceptorMap.size()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Interceptor</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Class</b></td>");
html.append("</tr>");
int id=1;
for(Class<? extends org.axe.interface_.mvc.Interceptor> interceptorClass:interceptorMap.keySet()){
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+interceptorClass.toString()+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/controller", method = RequestMethod.GET)
public void controller(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe controller</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
Set<Class<?>> controllerCLassSet = ClassHelper.getControllerClassSet();
html.append("<tr><td align=\"center\"><font size=\"28\">Controller set x"+controllerCLassSet.size()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Controller</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Title</b></td>");
html.append("<td align=\"left\"><b>BasePath</b></td>");
html.append("<td align=\"left\"><b>Class</b></td>");
html.append("<td align=\"left\"><b>Action</b></td>");
html.append("</tr>");
Map<String, List<Class<?>>> controllerClassMap = new HashMap<>();
for (Class<?> controllerClass : controllerCLassSet) {
Controller controller = controllerClass.getAnnotation(Controller.class);
String basePath = controller.basePath();
List<Class<?>> controllerClassList = new ArrayList<>();
if (controllerClassMap.containsKey(basePath)) {
controllerClassList = controllerClassMap.get(basePath);
} else {
controllerClassMap.put(basePath, controllerClassList);
}
controllerClassList.add(controllerClass);
}

List<String> basePathList = StringUtil.sortStringSet(controllerClassMap.keySet());
int id=1;
for (String basePath : basePathList) {
List<Class<?>> controllerClassList = controllerClassMap.get(basePath);
for (Class<?> controllerClass : controllerClassList) {
int actionCount = 0;
Method[] methodAry = controllerClass.getDeclaredMethods();
for (Method method : methodAry) {
if (method.isAnnotationPresent(Request.class)) {
actionCount++;
}
}

Controller controller = controllerClass.getAnnotation(Controller.class);
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+controller.desc()+"</td>");
html.append("<td align=\"left\">"+basePath+"</td>");
html.append("<td align=\"left\">"+controllerClass.getName()+"</td>");
String basePathHashCode = null;
int code = basePath.hashCode();
if(code < 0){
basePathHashCode = "_"+Math.abs(code);
}else{
basePathHashCode = String.valueOf(code);
}
html.append("<td align=\"left\">x<a href=\""+contextPath+"/axe/controller-"+basePathHashCode+"/action?token="+token+"\">"+actionCount+"</a></td>");
html.append("</tr>");
}
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/action", method = RequestMethod.GET)
public void action(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response, String basePathHashCode) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe action</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
List<Handler> handlerList = ControllerHelper.getActionList();
String basePath = "";
if (basePathHashCode != null) {
Map<Class<?>, String> hashCodeMap = new HashMap<>();
List<Handler> controllerHandlerList = new ArrayList<>();
for (Handler handler : handlerList) {
Class<?> controller = handler.getControllerClass();
String hashCode = null;
if (hashCodeMap.containsKey(controller)) {
hashCode = hashCodeMap.get(controller);
} else {
basePath = controller.getAnnotation(Controller.class).basePath();
int code = basePath.hashCode();
if (code < 0) {
hashCode = "_" + Math.abs(code);
} else {
hashCode = String.valueOf(code);
}
hashCodeMap.put(controller, hashCode);
}
if (hashCode.equals(basePathHashCode)) {
controllerHandlerList.add(handler);
}
}
handlerList = controllerHandlerList;
}
basePath = StringUtil.isEmpty(basePath)?"":basePath+" - ";
html.append("<tr><td align=\"center\"><font size=\"28\">"+basePath+"Action list x"+handlerList.size()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Action</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Title</b></td>");
html.append("<td align=\"left\"><b>Mapping</b></td>");
html.append("<td align=\"left\"><b>Request-Method</b></td>");
html.append("<td align=\"left\"><b>Controller-Class</b></td>");
html.append("<td align=\"left\"><b>Action-Method</b></td>");
html.append("<td align=\"left\"><b>Filter</b></td>");
html.append("</tr>");
Map<String, List<Handler>> handlerMap = new HashMap<>();
for (Handler handler : handlerList) {
String mappingPath = handler.getMappingPath();
List<Handler> action = new ArrayList<>();
if (handlerMap.containsKey(mappingPath)) {
action = handlerMap.get(mappingPath);
} else {
handlerMap.put(mappingPath, action);
}
action.add(handler);
}
List<String> mappingPathList = StringUtil.sortStringSet(handlerMap.keySet());
int id = 1;
for (String mappingPath : mappingPathList) {
List<Handler> action = handlerMap.get(mappingPath);
for (Handler handler : action) {
Controller controller = handler.getControllerClass().getAnnotation(Controller.class);
int code = controller.basePath().hashCode();
String hashCode = null;
if (code < 0) {
hashCode = "_" + Math.abs(code);
} else {
hashCode = String.valueOf(code);
}
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+handler.getMappingPathDesc()+"</td>");
html.append("<td align=\"left\">"+mappingPath+"</td>");
html.append("<td align=\"left\">"+handler.getRequestMethod()+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/controller-"+hashCode+"/action?token="+token+"\">"+handler.getControllerClass().getName()+"</a></td>");
hashCode = null;
code = (handler.getRequestMethod()+":"+mappingPath).hashCode();
if(code < 0){
hashCode = "_"+Math.abs(code);
}else{
hashCode = String.valueOf(code);
}
html.append("<td align=\"left\"><a href=\""+contextPath+"/axe/action/"+hashCode+"?token="+token+"\">"+handler.getActionMethod().getName()+"</a></td>");
html.append("<td align=\"left\">x"+handler.getFilterList().size()+"</td>");
html.append("</tr>");
}
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/controller-{basePathHashCode}/action", method = RequestMethod.GET)
public void action(@RequestParam("token")String token, @RequestParam("basePathHashCode") String basePathHashCode, HttpServletRequest request,
HttpServletResponse response) {
this.action(token, request, response, basePathHashCode);
}
@Request(path = "/action/{mappingHashCode}", method = RequestMethod.GET)
public void actionDetail(@RequestParam("mappingHashCode") String mappingHashCode, @RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
do {
if (StringUtil.isEmpty(mappingHashCode))
break;
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe action detail</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
List<Handler> handlerList = ControllerHelper.getActionList();
Handler handler = null;
for(Handler handler_:handlerList){
String hashCode = null;
int code = (handler_.getRequestMethod()+":"+handler_.getMappingPath()).hashCode();
if(code < 0){
hashCode = "_"+Math.abs(code);
}else{
hashCode = String.valueOf(code);
}
if(hashCode.equals(mappingHashCode)){
handler = handler_;
break;
}
}
if(handler == null) break;
html.append("<tr><td align=\"center\"><font size=\"28\">Action Detail - "+handler.getMappingPath()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Action Detail</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>属性</b></td>");
html.append("<td align=\"left\"><b>值</b></td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">Desc</td>");
html.append("<td align=\"left\">"+handler.getMappingPathDesc()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">Uri</td>");
html.append("<td align=\"left\">"+handler.getMappingPath()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">Method</td>");
html.append("<td align=\"left\">"+handler.getRequestMethod()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">Content-type</td>");
html.append("<td align=\"left\">"+handler.getContentType()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">Character-encoding</td>");
html.append("<td align=\"left\">"+handler.getCharacterEncoding()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">Class</td>");
html.append("<td align=\"left\">"+handler.getControllerClass().getName()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">Method</td>");
html.append("<td align=\"left\">"+handler.getActionMethod().toString()+"</td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Filter list</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Level</b></td>");
html.append("<td align=\"left\"><b>Class</b></td>");
html.append("<td align=\"left\"><b>Mapping</b></td>");
html.append("<td align=\"left\"><b>NotMapping</b></td>");
html.append("</tr>");
List<Filter> filterList = handler.getFilterList();
int id=1;
for(Filter filter:filterList){
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+filter.setLevel()+"</td>");
html.append("<td align=\"left\">"+filter.getClass()+"</td>");
Pattern mappingPattern = filter.setMapping();
String mappingRegex = mappingPattern == null?"":mappingPattern.toString();
html.append("<td align=\"left\">"+mappingRegex+"</td>");
Pattern notMappingPattern = filter.setNotMapping();
String notMappingRegex = notMappingPattern==null?"":notMappingPattern.toString();
html.append("<td align=\"left\">"+notMappingRegex+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Interceptor list</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Class</b></td>");
html.append("</tr>");
List<org.axe.interface_.mvc.Interceptor> interceptorList = handler.getInterceptorList();
id=1;
for(org.axe.interface_.mvc.Interceptor interceptor:interceptorList){
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+interceptor.getClass()+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
} while (false);
}
@Request(path = "/tns", method = RequestMethod.GET)
public void tns(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe tns</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
Set<Class<?>> serviceClassSet = ClassHelper.getClassSetByAnnotation(Service.class);
List<Method> tnsMethods = new ArrayList<>();
for(Class<?> serviceClass:serviceClassSet){
List<Method> methods = ReflectionUtil.getMethodByAnnotation(serviceClass, Tns.class);
if(CollectionUtil.isNotEmpty(methods)){
tnsMethods.addAll(methods);
}
}
html.append("<tr><td align=\"center\"><font size=\"28\">Tns point x"+tnsMethods.size()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Tns point</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Method</b></td>");
html.append("</tr>");
for(Method method:tnsMethods){
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">"+method.toString()+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/dao", method = RequestMethod.GET)
public void dao(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe dao</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
Set<Class<?>> daoClassSet = ClassHelper.getClassSetByAnnotation(Dao.class);
html.append("<tr><td align=\"center\"><font size=\"28\">Dao x"+daoClassSet.size()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Dao</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Method</b></td>");
html.append("</tr>");
for(Class<?> daoClass:daoClassSet){
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">"+daoClass.getName()+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/table", method = RequestMethod.GET)
public void table(
@RequestParam("token")String token,
@RequestParam(value = "s", required = true) String statics,
HttpServletRequest request,HttpServletResponse response) {
ContentType ct = ContentType.APPLICATION_HTML;
if (statics.endsWith(".html")) {
ct = ContentType.APPLICATION_HTML;
} else if (statics.endsWith(".js")) {
ct = ContentType.APPLICATION_JS;
} else if (statics.endsWith(".css")) {
ct = ContentType.APPLICATION_CSS;
}
printFile(response, "org/axe/home/rest/html/table/" + statics, ct, token);
}

@Request(path = "/table/table_list", method = RequestMethod.GET)
public List<Table> table_table_list(@RequestParam("token") String token, HttpServletRequest request,
HttpServletResponse response) {
try {
return TableExportUtil.exportTableList();
} catch (Exception e) {
e.printStackTrace();
throw new RestException(e.getMessage());
}
}
@Request(path = "/dataSource", method=RequestMethod.GET)
public void dataSource(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>axe datasource</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
if(ConfigHelper.getAxeSignIn()){
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe/sign-out?token="+token+"\"><b>退出</b></a>");
}
html.append("&nbsp;<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>首页</b></a>");
html.append("</td></tr>");
Map<String,BaseDataSource> dataSourceMap = DataSourceHelper.getDataSourceAll();
html.append("<tr><td align=\"center\"><font size=\"28\">DataSource x"+dataSourceMap.size()+"</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>DataSource</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>Name</b></td>");
html.append("<td align=\"left\"><b>Driver</b></td>");
html.append("<td align=\"left\"><b>Url</b></td>");
html.append("<td align=\"left\"><b>Username</b></td>");
html.append("<td align=\"left\"><b>Password</b></td>");
html.append("<td align=\"left\"><b>Class</b></td>");
html.append("</tr>");
for(Map.Entry<String,BaseDataSource> entry:dataSourceMap.entrySet()){
String dataSourceName = entry.getKey();
BaseDataSource dataSource = entry.getValue();
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">"+dataSourceName+"</td>");
html.append("<td align=\"left\">"+dataSource.setJdbcDriver()+"</td>");
html.append("<td align=\"left\">"+dataSource.setJdbcUrl()+"</td>");
html.append("<td align=\"left\">"+dataSource.setJdbcUserName()+"</td>");
html.append("<td align=\"left\">"+dataSource.setJdbcPassword()+"</td>");
html.append("<td align=\"left\">"+dataSource.getClass()+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(path = "/api_test", method = RequestMethod.GET)
public void api_test(
@RequestParam("token")String token,
@RequestParam(value = "s", required = true) String statics,
HttpServletRequest request,HttpServletResponse response) {
ContentType ct = ContentType.APPLICATION_HTML;
if (statics.endsWith(".html")) {
ct = ContentType.APPLICATION_HTML;
} else if (statics.endsWith(".js")) {
ct = ContentType.APPLICATION_JS;
} else if (statics.endsWith(".css")) {
ct = ContentType.APPLICATION_CSS;
}
printFile(response, "org/axe/home/rest/html/api_test/" + statics, ct, token);
}

@Request(path = "/api_test/api_list", method = RequestMethod.GET)
public List<Level_1> api_test_api_list(
@RequestParam("token")String token,
HttpServletRequest request, HttpServletResponse response) {
try {
String path = request.getContextPath();
String baseDomain = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
String basePath = baseDomain + path;
List<Level_1> list = ApiExportUtil.asApiTest(basePath);
return list;
} catch (Exception e) {
e.printStackTrace();
throw new RestException(e.getMessage());
}
}
}
