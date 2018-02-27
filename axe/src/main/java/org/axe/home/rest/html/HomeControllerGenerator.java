/**
 * MIT License
 * 
 * Copyright (c) 2017 The Axe Project
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
