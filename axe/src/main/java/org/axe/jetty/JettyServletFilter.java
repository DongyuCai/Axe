package org.axe.jetty;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.servlet.FilterHolder;

public final class JettyServletFilter {
	
	private FilterHolder holder;
	private String pathSpec;
	private EnumSet<DispatcherType> dispatches;
	
	
	public JettyServletFilter(FilterHolder holder, String pathSpec, EnumSet<DispatcherType> dispatches) {
		this.holder = holder;
		this.pathSpec = pathSpec;
		this.dispatches = dispatches;
	}
	
	public FilterHolder getHolder() {
		return holder;
	}
	public String getPathSpec() {
		return pathSpec;
	}
	public EnumSet<DispatcherType> getDispatches() {
		return dispatches;
	}
	
}
