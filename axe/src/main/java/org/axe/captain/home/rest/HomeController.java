package org.axe.captain.home.rest;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Interceptor;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.captain.bean.TeamTable;
import org.axe.constant.CharacterEncoding;
import org.axe.constant.ContentType;
import org.axe.constant.RequestMethod;
import org.axe.home.interceptor.HomeInterceptor;
import org.axe.home.interceptor.SignInInterceptor;
import org.axe.util.CollectionUtil;
import org.axe.util.HttpUtil;
import org.axe.util.JsonUtil;

@FilterFuckOff
@Interceptor({ HomeInterceptor.class, SignInInterceptor.class })
@Controller(basePath = "axe-captain")
public class HomeController {

private void printHtml(HttpServletResponse response, String html) {
try {
response.setCharacterEncoding(CharacterEncoding.UTF_8.CHARACTER_ENCODING);
response.setContentType(ContentType.APPLICATION_HTML.CONTENT_TYPE);
PrintWriter writer = response.getWriter();
writer.write(html);
writer.flush();
writer.close();
} catch (Exception e) {
e.printStackTrace();
}
}
@Request(value = "", method = RequestMethod.GET)
public void home(@RequestParam("token")String token, HttpServletRequest request, HttpServletResponse response) {
String contextPath = request.getContextPath();
StringBuilder html = new StringBuilder();
html.append("<!DOCTYPE html>");
html.append("<html>");
html.append("<head>");
html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
html.append("<title>captain homepage</title>");
html.append("<script type=\"text/javascript\">");
html.append("var refreshInt = setInterval(\"refresh()\",1000*60);");
html.append("function refresh(){");
html.append("window.location = \""+contextPath+"/axe-captain?token="+token+"\";");
html.append("}");
html.append("</script>");
html.append("</head>");
html.append("<body>");
html.append("<table width=\"100%\">");
html.append("<tr><td align=\"right\">");
html.append("<a style=\"font-size: 15px;color: #AE0000\" href=\""+contextPath+"/axe?token="+token+"\"><b>axe</b></a>");
html.append("</td></tr>");
html.append("<tr><td align=\"center\"><font size=\"28\">欢迎使用 Axe Captain!</font></td></tr>");
html.append("");
html.append("<!--系统运行 概览-->");
html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
html.append("&nbsp;<font color=\"white\"><b>Captain</b></font>&nbsp;");
html.append("</td></tr></table></td></tr>");
html.append("");
html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
html.append("<tr><td align=\"center\">");
List<String> teamTable = TeamTable.getTeamTableCopy();
if(CollectionUtil.isNotEmpty(teamTable)){
html.append("<table cellpadding=\"4px\" style=\"border-collapse: collapse;border-spacing: 0px\">");
html.append("<tr>");
for(String host:teamTable){
html.append("<td valign=\"top\">");
html.append("<table>");
html.append("<tr>");
html.append("<td style=\"background-color: #AE0000\" align=\"center\">");
html.append("&nbsp;<font color=\"white\"><b>"+host+"</b></font>&nbsp;");
html.append("</td>");
html.append("</tr>");
String teamTableUrl = host+"/axe-captain/teamTable";
try{
String result = HttpUtil.sendGet(teamTableUrl);
List<?> hosts = JsonUtil.fromJson(result,ArrayList.class);
for(Object obj:hosts){
html.append("<tr>");
html.append("<td style=\"background-color: #0000E3\" align=\"center\">");
html.append("&nbsp;<font color=\"white\"><b>"+String.valueOf(obj)+"</b></font>&nbsp;");
html.append("</td>");
html.append("</tr>");
}
}catch(Exception e){
html.append("<tr>");
html.append("<td style=\"background-color: #0000E3\" align=\"center\">");
html.append("通讯异常!");
html.append("</td>");
html.append("</tr>");
}
html.append("</table>");
html.append("</td>");
}
html.append("</tr>");
html.append("</table>");
} else {
html.append("无");
}
html.append("</td></tr><tr><td>&nbsp;</td></tr>");
html.append("");
html.append("</table>");
html.append("</body>");
html.append("</html>");
printHtml(response, html.toString());
}
}
