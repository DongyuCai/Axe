package org.axe.jetty;

import java.util.List;

import org.axe.DispatcherServlet;
import org.axe.util.CollectionUtil;
import org.axe.util.LogUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public final class JettyManager {
	
	/**
	 * 不带参数的启动，默认启动jetty
	 */
	public static void startJetty(int serverPort){
		startJetty(serverPort, "/", null);
	}
	
	/**
	 * 带参数启动jetty
	 * @param filterList 过滤器的链
	 */
	public static void startJetty(int serverPort, String contextPath, List<JettyServletFilter> filterList){
		try {
        	//启动jetty
        	Server server = new Server(serverPort);
            ServletContextHandler context = new ServletContextHandler();
            //设置上下文地址
            context.setContextPath(contextPath);
            server.setHandler(context);  
            
            //axe servlet
            ServletHolder axe = new ServletHolder(DispatcherServlet.class);
            axe.setInitOrder(1);//启动时加载
            context.addServlet(axe, "/*");
            
            //cross filter
            if(CollectionUtil.isNotEmpty(filterList)){
            	for(JettyServletFilter filter:filterList){
            		context.addFilter(filter.getHolder(), filter.getPathSpec(), filter.getDispatches());
            	}
            }
            
            //启动
            server.start(); 
            server.join();
            
            LogUtil.log(">>>>>>>>>\t Jetty server started success! \t<<<<<<<<<<");
		} catch (Exception e) {
			LogUtil.error(e);
			System.exit(1);
		}
	}
	
}
