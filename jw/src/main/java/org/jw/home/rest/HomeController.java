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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jw.annotation.ioc.Controller;
import org.jw.annotation.mvc.FilterFuckOff;
import org.jw.annotation.mvc.Request;
import org.jw.annotation.mvc.RequestParam;
import org.jw.annotation.persistence.Dao;
import org.jw.bean.mvc.Handler;
import org.jw.constant.RequestMethod;
import org.jw.helper.base.FrameworkStatusHelper;
import org.jw.helper.ioc.ClassHelper;
import org.jw.helper.mvc.ControllerHelper;
import org.jw.helper.mvc.FilterHelper;
import org.jw.helper.persistence.DataSourceHelper;
import org.jw.helper.persistence.TableHelper;
import org.jw.interface_.mvc.Filter;
import org.jw.interface_.persistence.DataSource;
import org.jw.util.HtmlUtil;
import org.jw.util.StringUtil;

@FilterFuckOff
@Controller(basePath = "/jw")
public class HomeController {

	@Request(value = "", method = RequestMethod.GET)
	public void home(HttpServletRequest request, HttpServletResponse response) {
		try {
			String contextPath = request.getContextPath();
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
			html.append("");
			html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
			html.append("&nbsp;<font color=\"white\"><b>系统运行-概览</b></font>&nbsp;");
			html.append("</td></tr></table></td></tr>");
			html.append("");
			html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
			html.append("<tr><td>");
			html.append("<table width=\"100%\">");
			html.append("<tr style=\"background-color: #F0F0F0;\">");
			html.append("<td align=\"center\">&nbsp;</td>");
			html.append("<td align=\"center\"><b>启动时间</b></td>");
			html.append("<td align=\"center\"><b>运行时长</b></td>");
			html.append("<td align=\"center\"><b>访问次数</b></td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td align=\"center\">&nbsp;</td>");
			Date startupTime = FrameworkStatusHelper.getStartupTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			html.append("<td align=\"center\">"+sdf.format(startupTime)+"</td>");
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
			runTimeMin = (runTimeSec-(runTimeHour * 60 * 60)) / 60;
			runTimeSec = runTimeSec - (runTimeHour * 60 * 60) - (runTimeMin * 60);
			runTime = runTimeHour + "时" + runTimeMin + "分" + runTimeSec + "秒";
			} else {
			long runTimeDay = runTimeHour / 24;
			runTimeHour = (runTimeSec-(runTimeDay * 24 * 60 * 60))/24;
			runTimeMin = (runTimeSec - (runTimeDay * 24 * 60 * 60) - (runTimeHour * 60 * 60))/60;
			runTimeSec = runTimeSec - (runTimeDay * 24 * 60 * 60) - (runTimeHour * 60 * 60)
			- (runTimeMin * 60);
			runTime = runTimeDay + "天" + runTimeHour + "时" + runTimeMin + "分" + runTimeSec + "秒";
			}
			}
			}
			html.append("<td align=\"center\">"+runTime+"</td>");
			html.append("<td align=\"center\"><a href=\"访问者ip详情列表\">;)</a></td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td></tr>");
			html.append("");
			html.append("<tr><td><table cellspacing=\"0px\"><tr><td style=\"background-color: #AE0000\">");
			html.append("&nbsp;<font color=\"white\"><b>MVC-概览</b></font>&nbsp;");
			html.append("</td></tr></table></td></tr>");
			html.append("");
			html.append("<tr><td height=\"2px\" style=\"background-color:#AE0000\"></td></tr>");
			html.append("<tr><td>");
			html.append("<table width=\"100%\">");
			html.append("<tr>");
			html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/filter\">Filter</a> x"+FilterHelper.getSortedFilterList().size()+"</td>");
			html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/controller\">Controller</a> x"+ClassHelper.getControllerClassSet().size()+"</td>");
			html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/action\">Action</a> x"+ClassHelper.getServiceClassSet().size()+"</td>");
			html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/service\">Service</a> x"+ControllerHelper.getActionList().size()+"</td>");
			html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/dao\">Dao</a> x"+ClassHelper.getClassSetByAnnotation(Dao.class).size()+"</td>");
			html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/table\">Table</a> x"+TableHelper.getEntityClassMap().size()+"</td>");
			html.append("<td align=\"center\"><a href=\""+contextPath+"/jw/datasource\">Datasource</a> x"+DataSourceHelper.getDataSourceAll().size()+"</td>");
			html.append("</tr>");
			html.append("</table>");
			html.append("</td></tr>");
			html.append("</table>");
			html.append("</body>");
			html.append("</html>");


			PrintWriter writer = response.getWriter();
			writer.write(html.toString());
			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Request(value = "/filter", method = RequestMethod.GET)
	public void filter(HttpServletResponse response) {
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
			html.append("<tr><td align=\"center\"><font size=\"28\">Filter list x" + filterList.size()
					+ "</font></td></tr>");
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
			int id = 1;
			for (Filter filter : filterList) {
				html.append("<tr>");
				html.append("<td align=\"left\">" + (id++) + "</td>");
				html.append("<td align=\"left\">" + filter.setLevel() + "</td>");
				html.append("<td align=\"left\">" + filter.getClass() + "</td>");
				Pattern mappingPattern = filter.setMapping();
				String mappingRegex = mappingPattern == null ? "" : mappingPattern.toString();
				html.append("<td align=\"left\">" + mappingRegex + "</td>");
				Pattern notMappingPattern = filter.setNotMapping();
				String notMappingRegex = notMappingPattern == null ? "" : notMappingPattern.toString();
				html.append("<td align=\"left\">" + notMappingRegex + "</td>");
				html.append("</tr>");
			}
			html.append("</table>");
			html.append("</td></tr>");
			html.append("</table>");
			html.append("</body>");
			html.append("</html>");

			PrintWriter writer = response.getWriter();
			writer.write(html.toString());
			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Request(value = "/controller", method = RequestMethod.GET)
	public void controller(HttpServletRequest request, HttpServletResponse response) {
		try {
			String contextPath = request.getContextPath();
			StringBuilder html = new StringBuilder();

			/**
			 * html代码粘贴区域，代码由本类静态方法生成
			 */
			html.append("<!DOCTYPE html>");
			html.append("<html>");
			html.append("<head>");
			html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			html.append("<title>jw controller</title>");
			html.append("</head>");
			html.append("<body>");
			html.append("<table width=\"100%\">");
			Set<Class<?>> controllerCLassSet = ClassHelper.getControllerClassSet();
			html.append("<tr><td align=\"center\"><font size=\"28\">Controller set x" + controllerCLassSet.size()
					+ "</font></td></tr>");
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
			int id = 1;
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
					html.append("<td align=\"left\">" + (id++) + "</td>");
					html.append("<td align=\"left\">" + basePath + "</td>");
					html.append("<td align=\"left\">" + controllerClass.getName() + "</td>");
					String basePathHashCode = null;
					int code = basePath.hashCode();
					if (code < 0) {
						basePathHashCode = "_" + Math.abs(code);
					} else {
						basePathHashCode = String.valueOf(code);
					}
					html.append("<td align=\"left\">x<a href=\"" + contextPath + "/jw/controller-" + basePathHashCode
							+ "/action\">" + actionCount + "</a></td>");
					html.append("</tr>");
				}
			}
			html.append("</table>");
			html.append("</td></tr>");
			html.append("</table>");
			html.append("</body>");
			html.append("</html>");

			PrintWriter writer = response.getWriter();
			writer.write(html.toString());
			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Request(value = "/action", method = RequestMethod.GET)
	public void action(HttpServletRequest request, HttpServletResponse response, String basePathHashCode) {
		try {
			String contextPath = request.getContextPath();
			StringBuilder html = new StringBuilder();

			/**
			 * html代码粘贴区域，代码由本类静态方法生成
			 */
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
			basePath = StringUtil.isEmpty(basePath) ? "" : basePath + " - ";
			html.append("<tr><td align=\"center\"><font size=\"28\">" + basePath + "Action list x" + handlerList.size()
					+ "</font></td></tr>");
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
					html.append("<td align=\"left\">" + (id++) + "</td>");
					html.append("<td align=\"left\">" + mappingPath + "</td>");
					html.append("<td align=\"left\">" + handler.getRequestMethod() + "</td>");
					html.append("<td align=\"left\"><a href=\"" + contextPath + "/jw/controller-" + hashCode
							+ "/action\">" + handler.getControllerClass().getName() + "</a></td>");
					hashCode = null;
					code = mappingPath.hashCode();
					if (code < 0) {
						hashCode = "_" + Math.abs(code);
					} else {
						hashCode = String.valueOf(code);
					}
					html.append("<td align=\"left\"><a href=\"" + contextPath + "/jw/action/" + hashCode + "\">"
							+ handler.getActionMethod().getName() + "</a></td>");
					html.append("<td align=\"left\">x" + handler.getFilterList().size() + "</td>");
					html.append("</tr>");
				}
			}
			html.append("</table>");
			html.append("</td></tr>");
			html.append("</table>");
			html.append("</body>");
			html.append("</html>");

			PrintWriter writer = response.getWriter();
			writer.write(html.toString());
			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
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

			try {
				StringBuilder html = new StringBuilder();

				/**
				 * html代码粘贴区域，代码由本类静态方法生成
				 */
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
				for (Handler handler_ : handlerList) {
					String hashCode = null;
					int code = handler_.getMappingPath().hashCode();
					if (code < 0) {
						hashCode = "_" + Math.abs(code);
					} else {
						hashCode = String.valueOf(code);
					}
					if (hashCode.equals(mappingHashCode)) {
						handler = handler_;
						break;
					}
				}
				if (handler == null)
					break;

				String basePath = handler.getControllerClass().getAnnotation(Controller.class).basePath();
				html.append("<tr><td align=\"center\"><font size=\"28\">Action Detail - " + handler.getMappingPath()
						+ "</font></td></tr>");
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
				html.append("<td align=\"left\">" + handler.getMappingPath() + "</td>");
				html.append("</tr>");
				html.append("<tr>");
				html.append("<td align=\"left\">&nbsp;</td>");
				html.append("<td align=\"left\">request-method</td>");
				html.append("<td align=\"left\">" + handler.getRequestMethod() + "</td>");
				html.append("</tr>");
				html.append("<tr>");
				html.append("<td align=\"left\">&nbsp;</td>");
				html.append("<td align=\"left\">content-type</td>");
				html.append("<td align=\"left\">" + handler.getContentType() + "</td>");
				html.append("</tr>");
				html.append("<tr>");
				html.append("<td align=\"left\">&nbsp;</td>");
				html.append("<td align=\"left\">character-encoding</td>");
				html.append("<td align=\"left\">" + handler.getCharacterEncoding() + "</td>");
				html.append("</tr>");
				html.append("<tr>");
				html.append("<td align=\"left\">&nbsp;</td>");
				html.append("<td align=\"left\">action-method</td>");
				html.append("<td align=\"left\">" + handler.getActionMethod().toString() + "</td>");
				html.append("</tr>");
				html.append("<tr>");
				html.append("<td align=\"left\">&nbsp;</td>");
				html.append("<td align=\"left\">basePath</td>");
				html.append("<td align=\"left\">" + basePath + "</td>");
				html.append("</tr>");
				html.append("<tr>");
				html.append("<td align=\"left\">&nbsp;</td>");
				html.append("<td align=\"left\">action-controller</td>");
				html.append("<td align=\"left\">" + handler.getControllerClass().getName() + "</td>");
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
				int id = 1;
				for (Filter filter : filterList) {
					html.append("<tr>");
					html.append("<td align=\"left\" style=\"background-color: #F0F0F0;\">" + (id++) + "</td>");
					html.append("<td align=\"left\">" + filter.setLevel() + "</td>");
					html.append("<td align=\"left\">" + filter.getClass() + "</td>");
					Pattern mappingPattern = filter.setMapping();
					String mappingRegex = mappingPattern == null ? "" : mappingPattern.toString();
					html.append("<td align=\"left\">" + mappingRegex + "</td>");
					Pattern notMappingPattern = filter.setNotMapping();
					String notMappingRegex = notMappingPattern == null ? "" : notMappingPattern.toString();
					html.append("<td align=\"left\">" + notMappingRegex + "</td>");
					html.append("</tr>");
				}
				html.append("</table>");
				html.append("</td></tr>");
				html.append("</table>");
				html.append("</body>");
				html.append("</html>");

				PrintWriter writer = response.getWriter();
				writer.write(html.toString());
				writer.flush();
				writer.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (false);
	}

	public static void main(String[] args) {
		// 从这里生成response包装页面代码
		HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/home.html");
		// HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/filter.html");
		// HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/controller.html");
		// HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/action.html");
		// HtmlUtil.convertHtmlCode("src/main/java/org/jw/home/rest/action_detail.html");
	}
}
