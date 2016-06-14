package test;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;


public class QuickStart {
	
	public static void main(String[] args) {
		new QuickStart().startJetty();
		
	}
	
	
	private static final int PORT = 8094;
	private static final String CONTEXT = "/";
	private static final String DEFAULT_WEBAPP_PATH = "./src/main/webapp";
	
	private static final String DESCRIPTOR = "./src/main/webapp/WEB-INF/web.xml";
	
	public static Server createServerInSource() {
		Server server = new Server();
		// 设置在JVM退出时关闭jetty的钩子.
		server.setStopAtShutdown(true);
		ServerConnector serverConnector = new ServerConnector(server);
		serverConnector.setPort(PORT);
		// 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
		serverConnector.setReuseAddress(false);
		server.setConnectors(new Connector[] {serverConnector});
		//设置idle超时(用户连接超时)
		serverConnector.setIdleTimeout(600000);
		WebAppContext webAppContext = new WebAppContext();

		webAppContext.setContextPath(CONTEXT);
		//servlet 3.0 不需要 web.xml
		webAppContext.setDescriptor(DESCRIPTOR);
		webAppContext.setResourceBase(DEFAULT_WEBAPP_PATH);
		server.setHandler(webAppContext);
		return server;
	}

	public void startJetty() {
		Server server = QuickStart.createServerInSource();
		try {
			server.start();
			System.out.println("HTTP-Service has been started.");
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}