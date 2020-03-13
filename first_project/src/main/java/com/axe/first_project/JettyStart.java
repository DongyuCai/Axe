package com.axe.first_project;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.axe.jetty.JettyManager;
import org.axe.jetty.JettyServletFilter;
import org.eclipse.jetty.servlet.FilterHolder;

import com.thetransactioncompany.cors.CORSFilter;

public class JettyStart {
	public static void main(String[] args) {
		int port = 80;
		if(args != null && args.length>0){
			port = Integer.parseInt(args[0]);
		}
		
        FilterHolder holder = new FilterHolder(CORSFilter.class);
        holder.setInitParameter("cors.allowGenericHttpRequests", "true");
        holder.setInitParameter("cors.allowOrigin", "*");
        holder.setInitParameter("cors.allowSubdomains", "false");
        holder.setInitParameter("cors.supportedMethods", "POST, DELETE, PUT, GET, HEAD, OPTIONS");
        holder.setInitParameter("cors.supportedHeaders", "*");
        holder.setInitParameter("cors.supportsCredentials", "true");
        holder.setInitParameter("cors.maxAge", "3600");
        
        List<JettyServletFilter> filterList = new ArrayList<>();
        filterList.add(new JettyServletFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST)));
        
		JettyManager.startJetty(port, "/", filterList);
	}
}
