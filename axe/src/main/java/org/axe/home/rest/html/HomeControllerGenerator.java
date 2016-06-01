package org.axe.home.rest.html;

import org.axe.util.Html2JavaUtil;

public class HomeControllerGenerator {

	public static void main(String[] args) {
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/home_controller_head.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_sign-in.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_filter.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_interceptor.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_controller.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_action.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_controller-$_action.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_action_$.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_tns.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_dao.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_dataSource.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_axe.properties.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_refresh_config.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/home_controller_foot.html");
	}
}
