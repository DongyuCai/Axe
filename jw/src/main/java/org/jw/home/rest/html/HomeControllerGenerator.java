package org.jw.home.rest.html;

import org.jw.util.Html2JavaUtil;

public class HomeControllerGenerator {

	public static void main(String[] args) {
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/home_controller_head.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_filter.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_interceptor.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_controller.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_action.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_controller-$_action.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_action_$.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_tns.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_dao.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/jw_dataSource.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/jw/home/rest/html/home_controller_foot.html");
	}
}
