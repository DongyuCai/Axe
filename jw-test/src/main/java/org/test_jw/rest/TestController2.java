package org.test_jw.rest;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.jw.annotation.ioc.Controller;
import org.jw.annotation.mvc.FilterFuckOff;
import org.jw.annotation.mvc.Request;
import org.jw.constant.RequestMethod;

@FilterFuckOff
@Controller(basePath = "test2")
public class TestController2 {

    @Request(value="page",method=RequestMethod.GET)
	public void test(HttpServletResponse response){
		System.out.println("/root");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write("/root");
			writer.flush();
		} catch (Exception e) {
			try {
				if(writer != null){
					writer.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
