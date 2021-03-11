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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.axe.annotation.ioc.Controller;
import org.axe.annotation.mvc.FilterFuckOff;
import org.axe.annotation.mvc.Interceptor;
import org.axe.annotation.mvc.Request;
import org.axe.annotation.mvc.RequestParam;
import org.axe.bean.mvc.FormParam;
import org.axe.bean.mvc.Handler;
import org.axe.bean.mvc.Param;
import org.axe.bean.mvc.View;
import org.axe.constant.CharacterEncoding;
import org.axe.constant.ContentType;
import org.axe.constant.RequestMethod;
import org.axe.exception.RestException;
import org.axe.helper.base.FrameworkStatusHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.helper.mvc.ControllerHelper;
import org.axe.helper.mvc.FilterHelper;
import org.axe.helper.mvc.InterceptorHelper;
import org.axe.home.interceptor.HomeInterceptor;
import org.axe.interface_.mvc.Filter;
import org.axe.util.ApiExportUtil;
import org.axe.util.ApiExportUtil.Action;
import org.axe.util.CollectionUtil;
import org.axe.util.LogUtil;
import org.axe.util.RequestUtil;
import org.axe.util.StringUtil;

@FilterFuckOff
@Interceptor({ HomeInterceptor.class })
@Controller(basePath = "/axe")
public final class AxeRest {

	@Request(path = "", method = RequestMethod.GET, desc = "首页跳转")
	public View index() {
		return new View("/axe/index.html");
	}

	@Request(path = "/controller_list", method = RequestMethod.GET, desc = "获取Controller列表")
	public List<Map<String, Object>> controller_list() {
		List<Map<String, Object>> result = new ArrayList<>();

		Set<Class<?>> controllerCLassSet = ClassHelper.getControllerClassSet();
		Map<String, List<Class<?>>> controllerClassMap = new HashMap<>();
		for (Class<?> controllerClass : controllerCLassSet) {
			Controller controller = controllerClass.getAnnotation(Controller.class);
			String basePath = controller.basePath();
			List<Class<?>> controllerClassList = controllerClassMap.get(basePath);
			if (controllerClassList == null) {
				controllerClassList = new ArrayList<>();
				controllerClassMap.put(basePath, controllerClassList);
			}

			controllerClassList.add(controllerClass);
		}

		List<String> basePathList = StringUtil.sortStringSet(controllerClassMap.keySet());
		for (String basePath : basePathList) {
			List<Class<?>> controllerClassList = controllerClassMap.get(basePath);
			for (Class<?> controllerClass : controllerClassList) {
				Map<String, Object> row = new HashMap<>();

				int actionSize = 0;
				Method[] methodAry = controllerClass.getDeclaredMethods();
				for (Method method : methodAry) {
					if (method.isAnnotationPresent(Request.class)) {
						actionSize++;
					}
				}

				Controller controller = controllerClass.getAnnotation(Controller.class);
				row.put("title", controller.desc());
				row.put("basePath", basePath);
				row.put("class", controllerClass.getName());
				row.put("actionSize", actionSize);
				result.add(row);
			}
		}

		return result;
	}

	@Request(path = "/action", method = RequestMethod.GET, desc = "获取Action")
	public Action action(@RequestParam(name = "actionIndex", required = true, desc = "action位置下标") Integer actionIndex,
			HttpServletRequest request) {
		List<Handler> actionList = ControllerHelper.getActionList();
		if(actionIndex<0 || actionIndex < actionList.size()){
			Handler handler = ControllerHelper.getActionList().get(actionIndex);
			if (handler != null) {
				return ApiExportUtil.getAction(RequestUtil.getBasePath(request), handler);
			} else {
				return new Action();
			}
		}else{
			throw new RestException(RestException.SC_NOT_FOUND,"资源不存在");
		}
	}

	@Request(path = "/action_list", method = RequestMethod.GET, desc = "获取Action列表")
	public List<Map<String, Object>> action_list(
			@RequestParam(name = "class", desc = "Controller类名") String className) {
		List<Map<String, Object>> result = new ArrayList<>();

		List<Handler> actionList = ControllerHelper.getActionList();
		Map<String, List<Handler>> actionMap = new HashMap<>();
		for (int i = 0; i < actionList.size(); i++) {
			Handler action = actionList.get(i);
			if (StringUtil.isNotEmpty(className)) {
				// 如果有参数className
				if (!action.getControllerClass().getName().equals(className)) {
					continue;
				}
			}

			String mappingPath = action.getMappingPath();
			List<Handler> handlerList = actionMap.get(mappingPath);
			if (handlerList == null) {
				handlerList = new ArrayList<>();
				actionMap.put(mappingPath, handlerList);
			}
			handlerList.add(action);
		}
		List<String> mappingPathList = StringUtil.sortStringSet(actionMap.keySet());
		for (String mappingPath : mappingPathList) {
			List<Handler> action = actionMap.get(mappingPath);
			for (Handler handler : action) {
				Map<String, Object> row = new HashMap<>();
				row.put("actionIndex", handler.getActionIndex());
				row.put("title", handler.getMappingPathDesc());
				row.put("requestMethod", handler.getRequestMethod());
				row.put("mappingPath", mappingPath);
				row.put("class", handler.getControllerClass().getName());
				row.put("action", handler.getActionMethod().getName());
				row.put("filterSize", handler.getFilterList().size());
				row.put("interceptorSize", handler.getInterceptorList().size());
				result.add(row);
			}
		}
		return result;
	}

	@Request(path = "/filter_list", method = RequestMethod.GET, desc = "获取过滤器列表")
	public List<Map<String, Object>> filter_list() {
		List<Map<String, Object>> result = new ArrayList<>();
		List<Filter> filterList = FilterHelper.getSortedFilterList();
		for (Filter filter : filterList) {
			Map<String, Object> row = new HashMap<>();
			row.put("level", filter.setLevel());
			row.put("class", filter.getClass().getName());
			row.put("actionSize", 0);
			if (FilterHelper.getActionSizeMap().containsKey(filter)) {
				row.put("actionSize", FilterHelper.getActionSizeMap().get(filter).size());
			}
			result.add(row);
		}
		return result;
	}

	@Request(path = "/filter_action_list", method = RequestMethod.GET, desc = "获取过滤器下的Action列表")
	public Map<String, Object> filter_action_list(
			@RequestParam(name = "index", required = true, desc = "过滤器位置下标") Integer index) {
		Map<String, Object> result = new HashMap<>();

		List<Map<String, Object>> result_actionList = new ArrayList<>();
		Filter filter = FilterHelper.getSortedFilterList().get(index);
		if (filter == null) {
			throw new RestException("下标越界");
		}
		Map<String, Object> result_filterMap = new HashMap<>();
		result_filterMap.put("level", filter.setLevel());
		result_filterMap.put("class", filter.getClass().getName());
		result.put("filter", result_filterMap);

		List<Handler> actionList = FilterHelper.getActionSizeMap().get(filter);
		Map<String, List<Handler>> actionMap = new HashMap<>();
		for (Handler action : actionList) {
			String mappingPath = action.getMappingPath();
			List<Handler> handlerList = actionMap.get(mappingPath);
			if (handlerList == null) {
				handlerList = new ArrayList<>();
				actionMap.put(mappingPath, handlerList);
			}
			handlerList.add(action);
		}
		List<String> mappingPathList = StringUtil.sortStringSet(actionMap.keySet());
		for (String mappingPath : mappingPathList) {
			List<Handler> action = actionMap.get(mappingPath);
			for (Handler handler : action) {
				Map<String, Object> row = new HashMap<>();
				row.put("title", handler.getMappingPathDesc());
				row.put("requestMethod", handler.getRequestMethod());
				row.put("mappingPath", handler.getMappingPath());
				row.put("class", handler.getControllerClass().getName());
				row.put("action", handler.getActionMethod().getName());
				result_actionList.add(row);
			}
		}

		result.put("actionList", result_actionList);

		return result;
	}

	@Request(path = "/interceptor_list", method = RequestMethod.GET, desc = "获取拦截器器列表")
	public List<Map<String, Object>> interceptor_list() {
		List<Map<String, Object>> result = new ArrayList<>();
		Collection<org.axe.interface_.mvc.Interceptor> interceptors = InterceptorHelper.getInterceptorMap().values();
		for (org.axe.interface_.mvc.Interceptor interceptor : interceptors) {
			Map<String, Object> row = new HashMap<>();
			row.put("class", interceptor.getClass().getName());
			row.put("actionSize", 0);
			if (InterceptorHelper.getActionSizeMap().containsKey(interceptor)) {
				row.put("actionSize", InterceptorHelper.getActionSizeMap().get(interceptor).size());
			}
			result.add(row);
		}
		return result;
	}

	@Request(path = "/interceptor_action_list", method = RequestMethod.GET, desc = "获取拦截器下的Action列表")
	public Map<String, Object> interceptor_action_list(
			@RequestParam(name = "class", required = true, desc = "拦截器类名") String className) {
		Map<String, Object> result = new HashMap<>();

		List<Map<String, Object>> result_interceptorList = new ArrayList<>();
		Map<Class<? extends org.axe.interface_.mvc.Interceptor>, org.axe.interface_.mvc.Interceptor> interceptorMap = InterceptorHelper
				.getInterceptorMap();
		for (Class<? extends org.axe.interface_.mvc.Interceptor> type : interceptorMap.keySet()) {
			if (type.getName().equals(className)) {
				org.axe.interface_.mvc.Interceptor interceptor = interceptorMap.get(type);
				Map<String, String> result_interceptorMap = new HashMap<>();
				result_interceptorMap.put("class", interceptor.getClass().getName());
				result.put("interceptor", result_interceptorMap);

				List<Handler> actionList = InterceptorHelper.getActionSizeMap().get(interceptor);
				Map<String, List<Handler>> actionMap = new HashMap<>();
				for (Handler action : actionList) {
					String mappingPath = action.getMappingPath();
					List<Handler> handlerList = actionMap.get(mappingPath);
					if (handlerList == null) {
						handlerList = new ArrayList<>();
						actionMap.put(mappingPath, handlerList);
					}
					handlerList.add(action);
				}
				List<String> mappingPathList = StringUtil.sortStringSet(actionMap.keySet());
				for (String mappingPath : mappingPathList) {
					List<Handler> action = actionMap.get(mappingPath);
					for (Handler handler : action) {
						Map<String, Object> row = new HashMap<>();
						row.put("title", handler.getMappingPathDesc());
						row.put("requestMethod", handler.getRequestMethod());
						row.put("mappingPath", handler.getMappingPath());
						row.put("class", handler.getControllerClass().getName());
						row.put("action", handler.getActionMethod().getName());
						result_interceptorList.add(row);
					}
				}

				break;
			}
		}
		result.put("interceptorList", result_interceptorList);
		return result;
	}

	@Request(path = "/status", method = RequestMethod.GET, desc = "获取系统状态")
	public Map<String, Object> status() {
		Map<String, Object> result = new HashMap<>();

		// 启动时间
		Date startupTime = FrameworkStatusHelper.getStartupTime();
		result.put("startTime", new SimpleDateFormat("MM-dd HH:mm:ss").format(startupTime));

		// 已经运行时长
		long runTimeSec = (System.currentTimeMillis() - startupTime.getTime()) / 1000;
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
					runTimeHour = runTimeHour - (runTimeDay * 24);
					runTimeMin = runTimeMin - ((runTimeDay * 24) + runTimeHour) * 60;
					runTimeSec = runTimeSec - ((((runTimeDay * 24) + runTimeHour) * 60) + runTimeMin) * 60;
					runTime = runTimeDay + "天" + runTimeHour + "时" + runTimeMin + "分" + runTimeSec + "秒";
				}
			}
		}
		result.put("runTime", runTime);

		// 系统负载
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage(); // 椎内存使用情况
		int M = 1024*1024;
		result.put("usedMemory", memoryUsage.getUsed()/M); // 已使用的内存
		result.put("maxMemory", memoryUsage.getMax()/M); // 最大可用内存

		// Filter数量
		result.put("filterSize", FilterHelper.getSortedFilterList().size());

		// Interceptor数量
		result.put("interceptorSize", InterceptorHelper.getInterceptorMap().size());

		// Controller数量
		int controllerSize = ClassHelper.getControllerClassSet().size();
		result.put("controllerSize", controllerSize);

		// Service数量
		Set<Class<?>> serviceClassSet = ClassHelper.getServiceClassSet();
		int serviceSize = serviceClassSet.size();
		result.put("serviceSize", serviceSize);

		// Component数量
		int componentSize = ClassHelper.getComponentClassSet().size();
		result.put("componentSize", componentSize);

		// Action数量
		result.put("actionSize", ControllerHelper.getActionList().size());

		return result;
	}

	/**
	 * 静态资源
	 */
	@Request(path = "/{static_resource}", method = RequestMethod.GET, desc = "目录：org/axe/home/rest/html/")
	public void root(@RequestParam(name = "static_resource", required = true, desc = "静态资源+路径") String static_resource,
			Param param, HttpServletRequest request, HttpServletResponse response) {
		outputResource(response, "org/axe/home/rest/html/" + static_resource, param.getFormParamList());
	}

	@Request(path = "/lib/{static_resource}", method = RequestMethod.GET, desc = "目录：org/axe/home/rest/html/lib/")
	public void lib(@RequestParam(name = "static_resource", required = true, desc = "静态资源+路径") String static_resource,
			Param param, HttpServletRequest request, HttpServletResponse response) {
		outputResource(response, "org/axe/home/rest/html/lib/" + static_resource, param.getFormParamList());
	}

	/**
	 * 静态资源输出
	 * 
	 * @throws Exception
	 */
	public void outputResource(HttpServletResponse response, String path, List<FormParam> paramList) {
		ContentType contentType = ContentType.APPLICATION_JSON;
		String pathLower = path.toLowerCase();
		if (pathLower.endsWith(".html")) {
			contentType = ContentType.APPLICATION_HTML;
		} else if (pathLower.endsWith(".txt")) {
			contentType = ContentType.APPLICATION_TXT;
		} else if (pathLower.endsWith(".xml")) {
			contentType = ContentType.APPLICATION_XML;
		} else if (pathLower.endsWith(".js")) {
			contentType = ContentType.APPLICATION_JS;
		} else if (pathLower.endsWith(".css")) {
			contentType = ContentType.APPLICATION_CSS;
		}else{
			//其他格式作为文件输出
			outputFile(response, path);
			return;
		}
		
		BufferedReader reader = null;
		InputStream in = null;
		try {
			File file = new File(path);
			if(file.exists()){
				in = new FileInputStream(file);
			}else{
				in = this.getClass().getClassLoader().getResourceAsStream(path);
			}
		} catch (Exception e) {
			try {
				if(in != null){
					in.close();
					in = null;
				}
			} catch (Exception e2) {}
		}
		if (in == null) {
			throw new RestException(RestException.SC_NOT_FOUND, "");
		}
		
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			response.setCharacterEncoding(CharacterEncoding.UTF_8.CHARACTER_ENCODING);
			response.setContentType(contentType.CONTENT_TYPE);
			ServletOutputStream out = response.getOutputStream();
			String line = reader.readLine();
			while (line != null) {
				try {
					if (CollectionUtil.isNotEmpty(paramList)) {
						for (FormParam param : paramList) {
							line = line.replaceAll("\\$\\{ *" + param.getFieldName() + " *\\}", param.getFieldValue());
						}

						// 全部参数都替换后，如果还有没被替换的，则为空串
						if (line.contains("${") && line.contains("}")) {
							line = line.replaceAll("\\$\\{ *[a-zA-Z0-9_]+ *\\}", "");
						}
					}
				} catch (Exception e) {
					LogUtil.error(e);
				}
				out.write((line + System.lineSeparator()).getBytes(CharacterEncoding.UTF_8.CHARACTER_ENCODING));
				line = reader.readLine();
			}
			// writer.flush();
			// writer.close();
		} catch (Exception e) {} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 静态资源输出
	 * @throws Exception
	 */
	private void outputFile(HttpServletResponse response, String path) {
		ContentType contentType = ContentType.APPLICATION_JSON;
		String pathLower = path.toLowerCase();
		if (pathLower.endsWith(".jpg") || pathLower.endsWith(".jpeg")) {
			contentType = ContentType.IMAGE_JPEG;
		} else if (pathLower.endsWith(".png")) {
			contentType = ContentType.IMAGE_PNG;
		} else if (pathLower.endsWith(".gif")) {
			contentType = ContentType.IMAGE_GIF;
		} else if (pathLower.endsWith(".ico")) {
			contentType = ContentType.IMAGE_ICON;
		} else if (pathLower.endsWith(".woff2")) {
			contentType = ContentType.FONT_WOFF2;
		} else if (pathLower.endsWith(".woff")) {
			contentType = ContentType.FONT_WOFF;
		} else if (pathLower.endsWith(".ttf")) {
			contentType = ContentType.FONT_TTF;
		} else if (pathLower.endsWith(".svg")) {
			contentType = ContentType.FONT_SVG;
		} else if (pathLower.endsWith(".eot")) {
			contentType = ContentType.FONT_EOT;
		} else if (pathLower.endsWith(".mp4")) {
			contentType = ContentType.VIDEO_MPEG4;
		} else if (pathLower.endsWith(".zip")){
			contentType = ContentType.ZIP_FILE;
		} else{
			throw new RestException("不支持的资源类型，无对应的contentType");
		}
		
		
		InputStream reader = null;
		try {
			File file = new File(path);
			if(file.exists()){
				reader = new FileInputStream(file);
			}else{
				reader = this.getClass().getClassLoader().getResourceAsStream(path);
			}
		} catch (Exception e) {
			try {
				if(reader != null){
					reader.close();
					reader = null;
				}
			} catch (Exception e2) {}
		}
		if (reader == null) {
			throw new RestException(RestException.SC_NOT_FOUND, "");
		}
		try {
			response.setCharacterEncoding(CharacterEncoding.UTF_8.CHARACTER_ENCODING);
			response.setContentType(contentType.CONTENT_TYPE);
			ServletOutputStream out = response.getOutputStream();
			byte[] data = new byte[1024];
			int len = reader.read(data);
			while (len > 0) {
				out.write(data,0,len);
				len = reader.read(data);
			}
		} catch (Exception e) {} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
