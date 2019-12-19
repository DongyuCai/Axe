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
package org.axe.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 日志工具
 * @author CaiDongyu
 */
public final class LogUtil {
	private static PrintStream sqlLogOut = null;
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	static{
		try {
			sqlLogOut = new PrintStream("sql_log.txt");
		} catch (Exception e) {
			error(e);
			sqlLogOut = System.out;//Default
		}
	}
	
	public static void error(Throwable error){
		String now = sdf.format(new Date());
		//控制台输出
		System.out.println(now+" - [ERROR] "+error.getMessage());
		error.printStackTrace();
		
		//准备记录到文件
		PrintStream defaultOut = System.out;//先保留原先输出
		System.setOut(sqlLogOut);//替换到文件输出
		
		System.out.println(now+" - [ERROR] "+error.getMessage());
		error.printStackTrace();
		
		//替换会原先输出
		System.setOut(defaultOut);
	}
	
	public static void error(String error){
		String now = sdf.format(new Date());
		//控制台输出
		System.out.println(now+" - [ERROR] "+error);
		
		//准备记录到文件
		PrintStream defaultOut = System.out;//先保留原先输出
		System.setOut(sqlLogOut);//替换到文件输出
		
		System.out.println(now+" - [ERROR] "+error);
		
		//替换会原先输出
		System.setOut(defaultOut);
	}
	
	public static void log(String msg){
		String now = sdf.format(new Date());
		//控制台输出
		System.out.println(now+" - "+msg);
		
		//准备记录到文件
		PrintStream defaultOut = System.out;//先保留原先输出
		System.setOut(sqlLogOut);//替换到文件输出
		
		System.out.println(now+" - "+msg);
		
		//替换会原先输出
		System.setOut(defaultOut);

	}
	
}
