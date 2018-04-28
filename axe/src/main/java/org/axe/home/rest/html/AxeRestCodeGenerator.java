/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.axe.util.Html2JavaUtil;

public class AxeRestCodeGenerator {

	public static void main(String[] args) {
		//开始
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File("src/main/java/org/axe/home/rest/AxeRest.java")));
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_rest_head.html",writer);
			
			//axe_home
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_sign-in.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_filter.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_interceptor.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_controller.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_action.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_controller-$_action.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_action_$.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_tns.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_dao.html",writer);
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_dataSource.html",writer);
//			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_axe.properties.html",writer);
//			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_refresh_config.html",writer);
			
			//功能模块
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/modules/api_test.html",writer);
			
			//结束
			Html2JavaUtil.convertHtmlCode("src/main/java/org/axe/home/rest/html/axe_home/axe_rest_foot.html",writer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(writer != null){
					writer.close();
				}
			} catch (Exception e2) {}
		}
		
	}
}
