package org.axe.captain.home.rest.html;

import org.axe.util.Html2JavaUtil;

public class HomeControllerGenerator {

	public static void main(String[] args) {
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/captain/home/rest/html/captain_controller_head.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/captain/home/rest/html/captain.html");
		Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/captain/home/rest/html/captain_controller_foot.html");
	}
}
