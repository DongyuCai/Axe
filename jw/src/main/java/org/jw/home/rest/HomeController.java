package org.jw.home.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.jw.annotation.ioc.Controller;
import org.jw.annotation.mvc.FilterFuckOff;
import org.jw.annotation.mvc.Request;
import org.jw.constant.RequestMethod;
import org.jw.util.StringUtil;

@FilterFuckOff
@Controller(basePath="/jw")
public class HomeController {
	
	public static void main(String[] args) {
		//从这里生成response包装页面代码
		homeHtmlCode();
	}
	
	private static void homeHtmlCode(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("src/main/java/org/jw/home/rest/home.html")));
			String line = null;
			while(!"quit".equals(line = reader.readLine())){
				line = StringUtil.isEmpty(line)?null:line.trim();
				if(StringUtil.isNotEmpty(line)){
					System.out.println("html.append(\""+line+"\");");
				}
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Request(value="",method=RequestMethod.GET)
	public void home(HttpServletResponse response){
		try {
			StringBuilder html = new StringBuilder();

			/**
			 * html代码粘贴区域，代码由本类静态方法生成
			 */
			html.append("<!doctype html>");
			html.append("<html>");
			html.append("<head>");
			html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			html.append("<title>jw homepage</title>");
			html.append("</head>");
			html.append("<body>");
			html.append("<table width=\"100%\">");
			html.append("<tr><td><font size=\"28\">Welcome to use jw!</font></td></tr>");
			html.append("<tr><td>");
			html.append("<table width=\"100%\">");
			html.append("<tr>");
			html.append("<td>Filter x1</td>");
			html.append("<td>Controller x2</td>");
			html.append("<td>Service x3</td>");
			html.append("<td>Action x6</td>");
			html.append("<td>Dao x4</td>");
			html.append("<td>Table x5</td>");
			html.append("<td>Datasource x7</td>");
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
	
}
