package org.jw.home.rest;

import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.jw.annotation.ioc.Controller;
import org.jw.annotation.mvc.FilterFuckOff;
import org.jw.annotation.mvc.Request;
import org.jw.annotation.persistence.Dao;
import org.jw.constant.RequestMethod;
import org.jw.helper.ioc.ClassHelper;
import org.jw.helper.mvc.ControllerHelper;
import org.jw.helper.mvc.FilterHelper;
import org.jw.helper.persistence.TableHelper;
import org.jw.interface_.mvc.Filter;
import org.jw.interface_.persistence.DataSource;
import org.jw.util.HtmlUtil;

@FilterFuckOff
@Controller(basePath="/jw")
public class HomeController {
	
	public static void main(String[] args) {
		//从这里生成response包装页面代码
//		HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/home.html");
//		HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/filter.html");
		HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/controller.html");
	}
	
	@Request(value="",method=RequestMethod.GET)
	public void home(HttpServletResponse response){
		try {
			StringBuilder html = new StringBuilder();

			/**
			 * html代码粘贴区域，代码由本类静态方法生成
			 */
			html.append("<!DOCTYPE html>");
			html.append("<html>");
			html.append("<head>");
			html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			html.append("<title>jw homepage</title>");
			html.append("</head>");
			html.append("<body>");
			html.append("<table width=\"100%\">");
			html.append("<tr><td align=\"center\"><font size=\"28\">Welcome to use jw!</font></td></tr>");
			html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
			html.append("<tr><td>");
			html.append("<table width=\"100%\">");
			html.append("<tr>");
			html.append("<td align=\"center\"><a href=\"/jw/filter\">Filter</a> x"+FilterHelper.getSortedFilterList().size()+"</td>");
			html.append("<td align=\"center\"><a href=\"/jw/controller\">Controller</a> x"+ClassHelper.getControllerClassSet().size()+"</td>");
			html.append("<td align=\"center\"><a href=\"/jw/service\">Service</a> x"+ControllerHelper.getActionList().size()+"</td>");
			html.append("<td align=\"center\"><a href=\"/jw/action\">Action</a> x"+ClassHelper.getServiceClassSet().size()+"</td>");
			html.append("<td align=\"center\"><a href=\"/jw/dao\">Dao</a> x"+ClassHelper.getClassSetByAnnotation(Dao.class).size()+"</td>");
			html.append("<td align=\"center\"><a href=\"/jw/table\">Table</a> x"+TableHelper.getEntityClassMap().size()+"</td>");
			html.append("<td align=\"center\"><a href=\"/jw/datasource\">Datasource</a> x"+ClassHelper.getClassSetBySuper(DataSource.class).size()+"</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td></tr>");
			html.append("</table>");
			html.append("</body>");
			html.append("</html>");
			
			PrintWriter writer  = response.getWriter();
			writer.write(html.toString());
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Request(value="/filter",method=RequestMethod.GET)
	public void filter(HttpServletResponse response){
		try {
			StringBuilder html = new StringBuilder();

			/**
			 * html代码粘贴区域，代码由本类静态方法生成
			 */
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
			html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
			html.append("<tr><td>");
			html.append("<table width=\"100%\">");
			html.append("<tr style=\"background-color: #F0F0F0;\">");
			html.append("<td align=\"center\"><b>Level</b></td>");
			html.append("<td align=\"center\"><b>Class</b></td>");
			html.append("<td align=\"center\"><b>Mapping</b></td>");
			html.append("<td align=\"center\"><b>NotMapping</b></td>");
			html.append("</tr>");
			for(Filter filter:filterList){
			html.append("<tr>");
			html.append("<td align=\"center\">"+filter.setLevel()+"</td>");
			html.append("<td align=\"center\">"+filter.getClass()+"</td>");
			Pattern mappingPattern = filter.setMapping();
			String mappingRegex = mappingPattern == null?"":mappingPattern.toString();
			html.append("<td align=\"center\">"+mappingRegex+"</td>");
			Pattern notMappingPattern = filter.setNotMapping();
			String notMappingRegex = notMappingPattern==null?"":notMappingPattern.toString();
			html.append("<td align=\"center\">"+notMappingRegex+"</td>");
			html.append("</tr>");
			}
			html.append("</table>");
			html.append("</td></tr>");
			html.append("</table>");
			html.append("</body>");
			html.append("</html>");
			
			PrintWriter writer  = response.getWriter();
			writer.write(html.toString());
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Request(value="/controller",method=RequestMethod.GET)
	public void controller(HttpServletResponse response){
		try {
			StringBuilder html = new StringBuilder();

			/**
			 * html代码粘贴区域，代码由本类静态方法生成
			 */
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
			html.append("<tr><td height=\"2px\" style=\"background-color: #AE0000\"></td></tr>");
			html.append("<tr><td>");
			html.append("<table width=\"100%\">");
			html.append("<tr style=\"background-color: #F0F0F0;\">");
			html.append("<td align=\"center\"><b>Level</b></td>");
			html.append("<td align=\"center\"><b>Class</b></td>");
			html.append("<td align=\"center\"><b>Mapping</b></td>");
			html.append("<td align=\"center\"><b>NotMapping</b></td>");
			html.append("</tr>");
			for(Filter filter:filterList){
			html.append("<tr>");
			html.append("<td align=\"center\">"+filter.setLevel()+"</td>");
			html.append("<td align=\"center\">"+filter.getClass()+"</td>");
			Pattern mappingPattern = filter.setMapping();
			String mappingRegex = mappingPattern == null?"":mappingPattern.toString();
			html.append("<td align=\"center\">"+mappingRegex+"</td>");
			Pattern notMappingPattern = filter.setNotMapping();
			String notMappingRegex = notMappingPattern==null?"":notMappingPattern.toString();
			html.append("<td align=\"center\">"+notMappingRegex+"</td>");
			html.append("</tr>");
			}
			html.append("</table>");
			html.append("</td></tr>");
			html.append("</table>");
			html.append("</body>");
			html.append("</html>");
			
			PrintWriter writer  = response.getWriter();
			writer.write(html.toString());
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
