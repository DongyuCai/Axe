package test;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QuickStart {
	
	public static void main(String[] args) {
		new QuickStart().startJetty();
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuickStart.class);
	
	private static final int PORT = 8080;
	private static final String CONTEXT = "/";
	private static final String DEFAULT_WEBAPP_PATH = "./src/main/webapp";
	
//	private static final String DESCRIPTOR = "./src/main/webapp/WEB-INF/web.xml";
	
	public static Server createServerInSource(int port, String contextPath) {
		Server server = new Server();
		// 设置在JVM退出时关闭jetty的钩子.
		server.setStopAtShutdown(true);
		ServerConnector serverConnector = new ServerConnector(server);
		serverConnector.setPort(port);
		// 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
		serverConnector.setReuseAddress(false);
		server.setConnectors(new Connector[] {serverConnector});
		//设置idle超时(用户连接超时)
		serverConnector.setIdleTimeout(600000);
		WebAppContext webAppContext = new WebAppContext();

		webAppContext.setContextPath(CONTEXT);
		//servlet 3.0 不需要 web.xml
//		webAppContext.setDescriptor(DESCRIPTOR);
		webAppContext.setResourceBase(DEFAULT_WEBAPP_PATH);
		server.setHandler(webAppContext);
		return server;
	}

	public void startJetty() {
		Server server = QuickStart.createServerInSource(PORT, CONTEXT);
		try {
			server.start();
			LOGGER.info("HTTP-Service has been started.");
			server.join();
		} catch (Exception e) {
			LOGGER.error("HTTP-Service start failure",e);
			System.exit(-1);
		}
	}

}