package org.jw.home.rest;

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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.annotation.ioc.Autowired;
import org.jw.annotation.ioc.Controller;
import org.jw.annotation.ioc.Service;
import org.jw.annotation.mvc.FilterFuckOff;
import org.jw.annotation.mvc.Interceptor;
import org.jw.annotation.mvc.Request;
import org.jw.annotation.mvc.RequestParam;
import org.jw.annotation.persistence.Dao;
import org.jw.annotation.persistence.Tns;
import org.jw.bean.mvc.Handler;
import org.jw.bean.mvc.Param;
import org.jw.constant.CharacterEncoding;
import org.jw.constant.ConfigConstant;
import org.jw.constant.ContentType;
import org.jw.constant.RequestMethod;
import org.jw.helper.HelperLoader;
import org.jw.helper.base.ConfigHelper;
import org.jw.helper.base.FrameworkStatusHelper;
import org.jw.helper.ioc.ClassHelper;
import org.jw.helper.mvc.ControllerHelper;
import org.jw.helper.mvc.FilterHelper;
import org.jw.helper.mvc.InterceptorHelper;
import org.jw.helper.persistence.DataSourceHelper;
import org.jw.helper.persistence.TableHelper;
import org.jw.home.interceptor.HomeInterceptor;
import org.jw.home.service.HomeService;
import org.jw.interface_.mvc.Filter;
import org.jw.interface_.persistence.DataSource;
import org.jw.util.CollectionUtil;
import org.jw.util.ReflectionUtil;
import org.jw.util.StringUtil;

@FilterFuckOff
@Interceptor(HomeInterceptor.class)
@Controller(basePath = "/jw")
public class HomeController {

@Autowired
private HomeService homeService;

private void printHtml(HttpServletResponse response, String html) {
try {
response.setCharacterEncoding(CharacterEncoding.UTF_8);
response.setContentType(ContentType.APPLICATION_HTML);
PrintWriter writer = response.getWriter();
writer.write(html);
writer.flush();
writer.close();
} catch (Exception e) {
e.printStackTrace();
}
}
@Request(value = "", method = RequestMethod.GET)
public void home(HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw homepage</title>");
html.append("<script type=\"text/javascript\">");
html.append("var refreshInt = setInterval(\"refresh()\",1000*60);");
html.append("function refresh(){");
html.append("window.location = \""+contextPath+"/jw\";");
html.append("}");
html.append("</script>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"center\"><font size=\"28\">欢迎使用 jw!</font></td></tr>");
html.append("");
html.append("<!--系统运行 概览-->");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>系统运行-概览</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>启动时间</b></td>");
html.append("<td align=\"left\"><b>运行时长</b></td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
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
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>系统参数 jw.properties</b></font>&nbsp;");
html.append("</td><td>&nbsp;</td><td style=\"background-color: #007500;cursor: pointer;\" onclick=\"window.location='"+contextPath+"/jw/jw.properties'\">");
html.append("&nbsp;<font color=\"white\"><b>编辑</b></font>&nbsp;");
html.append("</td><td>&nbsp;</td><td style=\"background-color: #007500;cursor: pointer;\" onclick=\"window.location='"+contextPath+"/jw/refresh-config'\">");
html.append("&nbsp;<font color=\"white\"><b>重载 jw.properties 配置</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>参数-键</b></td>");
html.append("<td align=\"left\"><b>参数-值</b></td>");
html.append("<td align=\"left\"><b>参数描述</b></td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jw.home</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJwHome()+"</td>");
html.append("<td align=\"left\">是否开启/jw的访问</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jw.classhelper.keep</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJwClassHelperKeep()+"</td>");
html.append("<td align=\"left\">启动后是否释放ClassHelper的内存(释放后ClassHelper不可再用)</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.driver</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcDriver()+"</td>");
html.append("<td align=\"left\">jdbc-driver</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.url</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcUrl()+"</td>");
html.append("<td align=\"left\">jdbc-url</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.username</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcUsername()+"</td>");
html.append("<td align=\"left\">jdbc-username</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.password</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcPassword()+"</td>");
html.append("<td align=\"left\">jdbc-password</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.datasource</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcDatasource()+"</td>");
html.append("<td align=\"left\">指定DataSource实现类</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.base_package</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppBasePackage()+"</td>");
html.append("<td align=\"left\">指定框架扫描的包路径，多个路径使用“,”号分割</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.jsp_path</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppJspPath()+"</td>");
html.append("<td align=\"left\">指定jsp存放路径</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.asset_path</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppAssetPath()+"</td>");
html.append("<td align=\"left\">指定静态文件(html、js、css、图片等)存放路径</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.upload_limit</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppUploadLimit()+"</td>");
html.append("<td align=\"left\">文件上传限制单次文件大小，单位M，默认0不限制</td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("");
html.append("<!--MVC 概览-->");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>MVC-概览</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<table width=\"100%\">");
html.append("<tr>");
html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/filter\">Filter</a> x"+FilterHelper.getSortedFilterList().size()+"</td>");
html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/interceptor\">Interceptor</a> x"+InterceptorHelper.getInterceptorMap().size()+"</td>");
String controllerSize = "?";
String serviceSize = "?";
String tnsPointCount = "?";
String daoSize = "?";
if(ConfigHelper.getJwClassHelperKeep()){
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
html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/controller\">Controller</a> x"+controllerSize+"</td>");
html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/action\">Action</a> x"+ControllerHelper.getActionList().size()+"</td>");
html.append("<td align=\"center\">Service x"+serviceSize+"</td>");
html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/tns\">Tns point </a> x"+tnsPointCount+"</td>");
html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/dao\">Dao</a> x"+daoSize+"</td>");
html.append("<td align=\"center\">Table</a> x"+TableHelper.getEntityClassMap().size()+"</td>");
html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/datasource\">Datasource</a> x"+DataSourceHelper.getDataSourceAll().size()+"</td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(value = "/filter", method = RequestMethod.GET)
public void filter(HttpServletResponse response) {
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw filter</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
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
@Request(value = "/interceptor", method = RequestMethod.GET)
public void interceptor(HttpServletResponse response) {
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw interceptor</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
Map<Class<? extends org.jw.interface_.mvc.Interceptor>,org.jw.interface_.mvc.Interceptor> interceptorMap = InterceptorHelper.getInterceptorMap();
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
for(Class<? extends org.jw.interface_.mvc.Interceptor> interceptorClass:interceptorMap.keySet()){
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
@Request(value = "/controller", method = RequestMethod.GET)
public void controller(HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw controller</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
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
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+basePath+"</td>");
html.append("<td align=\"left\">"+controllerClass.getName()+"</td>");
String basePathHashCode = null;
int code = basePath.hashCode();
if(code < 0){
basePathHashCode = "_"+Math.abs(code);
}else{
basePathHashCode = String.valueOf(code);
}
html.append("<td align=\"left\">x<a href=\""+contextPath+"/jw/controller-"+basePathHashCode+"/action\">"+actionCount+"</a></td>");
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
@Request(value = "/action", method = RequestMethod.GET)
public void action(HttpServletRequest request, HttpServletResponse response, String basePathHashCode) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw action</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
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
int code = handler.getControllerClass().getAnnotation(Controller.class).basePath().hashCode();
String hashCode = null;
if (code < 0) {
hashCode = "_" + Math.abs(code);
} else {
hashCode = String.valueOf(code);
}
html.append("<tr>");
html.append("<td align=\"left\">"+(id++)+"</td>");
html.append("<td align=\"left\">"+mappingPath+"</td>");
html.append("<td align=\"left\">"+handler.getRequestMethod()+"</td>");
html.append("<td align=\"left\"><a href=\""+contextPath+"/jw/controller-"+hashCode+"/action\">"+handler.getControllerClass().getName()+"</a></td>");
hashCode = null;
code = mappingPath.hashCode();
if(code < 0){
hashCode = "_"+Math.abs(code);
}else{
hashCode = String.valueOf(code);
}
html.append("<td align=\"left\"><a href=\""+contextPath+"/jw/action/"+hashCode+"\">"+handler.getActionMethod().getName()+"</a></td>");
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
@Request(value = "/controller-{basePathHashCode}/action", method = RequestMethod.GET)
public void action(@RequestParam("basePathHashCode") String basePathHashCode, HttpServletRequest request,
HttpServletResponse response) {
this.action(request, response, basePathHashCode);
}
@Request(value = "/action/{mappingHashCode}", method = RequestMethod.GET)
public void actionDetail(@RequestParam("mappingHashCode") String mappingHashCode, HttpServletResponse response) {
do {
if (StringUtil.isEmpty(mappingHashCode))
break;
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw action detail</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
List<Handler> handlerList = ControllerHelper.getActionList();
Handler handler = null;
for(Handler handler_:handlerList){
String hashCode = null;
int code = handler_.getMappingPath().hashCode();
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

String basePath = handler.getControllerClass().getAnnotation(Controller.class).basePath();
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
html.append("<td align=\"left\">mapping</td>");
html.append("<td align=\"left\">"+handler.getMappingPath()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">request-method</td>");
html.append("<td align=\"left\">"+handler.getRequestMethod()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">content-type</td>");
html.append("<td align=\"left\">"+handler.getContentType()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">character-encoding</td>");
html.append("<td align=\"left\">"+handler.getCharacterEncoding()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">action-method</td>");
html.append("<td align=\"left\">"+handler.getActionMethod().toString()+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">basePath</td>");
html.append("<td align=\"left\">"+basePath+"</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">action-controller</td>");
html.append("<td align=\"left\">"+handler.getControllerClass().getName()+"</td>");
html.append("</tr>");
html.append("</table>");
html.append("</td></tr>");
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
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
} while (false);
}
@Request(value = "/tns", method = RequestMethod.GET)
public void tns(HttpServletResponse response) {
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw tns</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
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
@Request(value = "/dao", method = RequestMethod.GET)
public void dao(HttpServletResponse response) {
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw dao</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
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
@Request(value="/dataSource", method=RequestMethod.GET)
public void dataSource(HttpServletResponse response){
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw datasource</title>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
Map<String,DataSource> dataSourceMap = DataSourceHelper.getDataSourceAll();
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
html.append("<td align=\"left\"><b>Class</b></td>");
html.append("<td align=\"left\"><b>Name</b></td>");
html.append("</tr>");
for(Map.Entry<String,DataSource> entry:dataSourceMap.entrySet()){
DataSource dataSource = entry.getValue();
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">"+dataSource.getClass()+"</td>");
html.append("<td align=\"left\">"+dataSource.setName()+"</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(value = "/jw.properties", method = RequestMethod.GET)
public void jwProperties(HttpServletRequest request, HttpServletResponse response, String basePathHashCode) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw jw.properties 配置</title>");
html.append("<script type=\"text/javascript\">");
html.append("var config = {");
html.append("'jw.home':'"+ConfigHelper.getJwClassHelperKeep()+"',");
html.append("'jw.classhelper.keep':'"+ConfigHelper.getJwClassHelperKeep()+"',");
html.append("'jdbc.driver':'"+ConfigHelper.getJdbcDriver()+"',");
html.append("'jdbc.url':'"+ConfigHelper.getJdbcUrl()+"',");
html.append("'jdbc.username':'"+ConfigHelper.getJdbcUsername()+"',");
html.append("'jdbc.password':'"+ConfigHelper.getJdbcPassword()+"',");
html.append("'jdbc.datasource':'"+ConfigHelper.getJdbcDatasource()+"',");
html.append("'app.base_package':'"+ConfigHelper.getAppBasePackage()+"',");
html.append("'app.jsp_path':'"+ConfigHelper.getAppJspPath()+"',");
html.append("'app.asset_path':'"+ConfigHelper.getAppAssetPath()+"',");
html.append("'app.upload_limit':'"+ConfigHelper.getAppUploadLimit()+"'");
html.append("};");
html.append("");
html.append("function setProperty(property,value){");
html.append("console.log(property+'=='+config[property]+'=='+value);");
html.append("config[property] = value;");
html.append("}");
html.append("");
html.append("function saveProperties(){");
html.append("if(!config['jw.home'] || config['jw.home'] == 'null' || config['jw.home'] == null || config['jw.home'] == ''){");
html.append("alert('jw.home 必填');");
html.append("return false;");
html.append("}");
html.append("if(!config['jw.classhelper.keep'] || config['jw.classhelper.keep'] == 'null' || config['jw.classhelper.keep'] == null || config['jw.classhelper.keep'] == ''){");
html.append("alert('jw.classhelper.keep 必填');");
html.append("return false;");
html.append("}");
html.append("if(!config['jdbc.datasource'] || config['jdbc.datasource'] == 'null' || config['jdbc.datasource'] == null || config['jdbc.datasource'] == ''){");
html.append("if(!config['jdbc.driver'] || config['jdbc.driver'] == 'null' || config['jdbc.driver'] == null || config['jdbc.driver'] == ''){");
html.append("alert('jdbc.driver 必填');");
html.append("return false;");
html.append("}");
html.append("if(!config['jdbc.url'] || config['jdbc.url'] == 'null' || config['jdbc.url'] == null || config['jdbc.url'] == ''){");
html.append("alert('jdbc.url 必填');");
html.append("return false;");
html.append("}");
html.append("if(!config['jdbc.username'] || config['jdbc.username'] == 'null' || config['jdbc.username'] == null || config['jdbc.username'] == ''){");
html.append("alert('jdbc.username 必填');");
html.append("return false;");
html.append("}");
html.append("if(!config['jdbc.password'] || config['jdbc.password'] == 'null' || config['jdbc.password'] == null || config['jdbc.password'] == ''){");
html.append("alert('jdbc.password 必填');");
html.append("return false;");
html.append("}");
html.append("}");
html.append("if(!config['app.base_package'] || config['app.base_package'] == 'null' || config['app.base_package'] == null || config['app.base_package'] == ''){");
html.append("alert('app.base_package 必填');");
html.append("return false;");
html.append("}");
html.append("");
html.append("var saveForm = document.getElementById(\"saveForm\");");
html.append("saveForm.submit();");
html.append("}");
html.append("</script>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"center\"><font size=\"28\">jw.properties</font></td></tr>");
html.append("");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>修改并生成新的配置</b></font>&nbsp;");
html.append("</td><td>&nbsp;</td><td style=\"background-color: #007500;cursor: pointer;\" onclick=\"saveProperties()\">");
html.append("&nbsp;<font color=\"white\"><b>保存</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
html.append("<tr><td>");
html.append("<form id=\"saveForm\" method=\"POST\" action=\""+contextPath+"/jw/jw.properties\">");
html.append("<table width=\"100%\">");
html.append("<tr style=\"background-color: #F0F0F0;\">");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\"><b>参数-键</b></td>");
html.append("<td align=\"left\"><b>参数-值</b></td>");
html.append("<td align=\"left\"><b>参数描述</b></td>");
html.append("<td align=\"left\"><b>调整值</b></td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jw.home</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJwHome()+"</td>");
html.append("<td align=\"left\">是否开启/jw的访问</td>");
html.append("<td align=\"left\">");
html.append("<select name=\"jw.home\" onchange=\"setProperty('jw.home',this.value)\">");
html.append("<option value=\"true\"");
if(ConfigHelper.getJwHome()){
html.append("selected=\"true\"");
}
html.append(">是</option>");
html.append("<option value=\"false\"");
if(!ConfigHelper.getJwHome()){
html.append("selected=\"true\"");
}
html.append(">否</option>");
html.append("</select>");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jw.classhelper.keep</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJwClassHelperKeep()+"</td>");
html.append("<td align=\"left\">启动后是否释放ClassHelper的内存(释放后ClassHelper不可再用)</td>");
html.append("<td align=\"left\">");
html.append("<select name=\"jw.classhelper.keep\" onchange=\"setProperty('jw.classhelper.keep',this.value)\">");
html.append("<option value=\"true\"");
if(ConfigHelper.getJwClassHelperKeep()){
html.append("selected=\"true\"");
}
html.append(">是</option>");
html.append("<option value=\"false\"");
if(!ConfigHelper.getJwClassHelperKeep()){
html.append("selected=\"true\"");
}
html.append(">否</option>");
html.append("</select>");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.driver</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcDriver()+"</td>");
html.append("<td align=\"left\">jdbc-driver</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"jdbc.driver\" type=\"text\" value=\""+ConfigHelper.getJdbcDriver()+"\" onchange=\"setProperty('jdbc.driver',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.url</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcUrl()+"</td>");
html.append("<td align=\"left\">jdbc-url</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"jdbc.url\" type=\"text\" value=\""+ConfigHelper.getJdbcUrl()+"\" onchange=\"setProperty('jdbc.url',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.username</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcUsername()+"</td>");
html.append("<td align=\"left\">jdbc-username</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"jdbc.username\" type=\"text\" value=\""+ConfigHelper.getJdbcUsername()+"\" onchange=\"setProperty('jdbc.username',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.password</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcPassword()+"</td>");
html.append("<td align=\"left\">jdbc-password</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"jdbc.password\" type=\"text\" value=\""+ConfigHelper.getJdbcPassword()+"\" onchange=\"setProperty('jdbc.password',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">jdbc.datasource</td>");
html.append("<td align=\"left\">"+ConfigHelper.getJdbcDatasource()+"</td>");
html.append("<td align=\"left\">指定DataSource实现类</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"jdbc.datasource\" type=\"text\" value=\""+ConfigHelper.getJdbcDatasource()+"\" onchange=\"setProperty('jdbc.datasource',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.base_package</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppBasePackage()+"</td>");
html.append("<td align=\"left\">指定框架扫描的包路径，多个路径使用“,”号分割</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"app.base_package\" type=\"text\" value=\""+ConfigHelper.getAppBasePackage()+"\" onchange=\"setProperty('app.base_package',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.jsp_path</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppJspPath()+"</td>");
html.append("<td align=\"left\">指定jsp存放路径</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"app.jsp_path\" type=\"text\" value=\""+ConfigHelper.getAppJspPath()+"\" onchange=\"setProperty('app.jsp_path',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.asset_path</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppAssetPath()+"</td>");
html.append("<td align=\"left\">指定静态文件(html、js、css、图片等)存放路径</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"app.asset_path\" type=\"text\" value=\""+ConfigHelper.getAppAssetPath()+"\" onchange=\"setProperty('app.asset_path',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("<tr>");
html.append("<td align=\"left\">&nbsp;</td>");
html.append("<td align=\"left\">app.upload_limit</td>");
html.append("<td align=\"left\">"+ConfigHelper.getAppUploadLimit()+"</td>");
html.append("<td align=\"left\">文件上传限制单次文件大小，单位M，默认0不限制</td>");
html.append("<td align=\"left\">");
html.append("<input name=\"app.upload_limit\" type=\"text\" value=\""+ConfigHelper.getAppUploadLimit()+"\" onchange=\"setProperty('app.upload_limit',this.value)\" />");
html.append("</td>");
html.append("</tr>");
html.append("</table>");
html.append("</form>");
html.append("</td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(value = "/jw.properties", method = RequestMethod.POST)
public void jwProperties(HttpServletRequest request, HttpServletResponse response,Param param) {
String contextPath = request.getContextPath();

homeService.saveJwProperties(param);

StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw save properties</title>");
html.append("<script type=\"text/javascript\">");
html.append("var number = 10;");
html.append("document.getElementById(\"number\").innerHTML = number;");
html.append("");
html.append("var int=self.setInterval(\"toHome()\",1000);");
html.append("");
html.append("function toHome(){");
html.append("number = number-1;");
html.append("document.getElementById(\"number\").innerHTML = number;");
html.append("if(number <= 0){");
html.append("window.clearInterval(int);");
html.append("window.location = \""+contextPath+"/jw\";");
html.append("}");
html.append("}");
html.append("");
html.append("</script>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"center\"><span id=\"number\">10</span>秒后自动跳转<a href=\""+contextPath+"/jw\">/jw首页</a></td></tr>");
html.append("<tr><td align=\"center\"><font size=\"28\"><b>保存配置成功，请查看 classes 目录下"+ConfigConstant.CONFIG_FILE+"</b></font></td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
@Request(value = "/refresh-config", method = RequestMethod.GET)
public void refreshConfig(HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();

ServletContext servletContext = request.getServletContext();
HelperLoader.refresHelpers(servletContext);

StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>jw save properties</title>");
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
html.append("window.location = \""+contextPath+"/jw\";");
html.append("}");
html.append("}");
html.append("");
html.append("</script>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"center\"><span id=\"number\">10</span>秒后自动跳转<a href=\""+contextPath+"/jw\">/jw首页</a></td></tr>");
html.append("<tr><td align=\"center\"><font size=\"28\"><b>刷新配置成功！</b></font></td></tr>");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
}
