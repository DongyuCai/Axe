package org.axe.jetty;

import java.util.List;

import org.axe.DispatcherServlet;
import org.axe.util.CollectionUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyManager {
	
	/**
	 * 
	 */
	public void startJetty(){
		startJetty("/", null);
	}
	
	/**
	 * 带参数启动jetty
	 * @param filterList 过滤器的链
	 */
	public void startJetty(String contextPath,List<JettyServletFilter> filterList){
		try {
        	//启动jetty
        	Server server = new Server(80);
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
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
