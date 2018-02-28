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
/**
 * 解析html输出成java代码
 * @author CaiDongyu on 2016/4/25.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Html2JavaUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(Html2JavaUtil.class);
	
	public static void convertHtmlCode(String htmlFileName){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(htmlFileName)));
			String line = null;
			boolean javaTag = false;
			while((line = reader.readLine()) != null){
				line = StringUtil.isEmpty(line)?"":line.trim();
//				if(StringUtil.isNotEmpty(line)){
					//解析第三部，判断是否是java代码
					if(line.startsWith("<java>")){
						javaTag = true;
						continue;
					}
					if(line.startsWith("</java>")){
						javaTag = false;
						continue;
					}
				
					if(javaTag){
						System.out.println(line);
					}else{
						//解析第一步，先将所有双引号转意
						line = line.replaceAll("\"", "\\\\\"");
						
						//解析第二部，将不需要转意的双引号，再转回来
						if(line.contains("\\\"+")){
							line = line.replaceAll("\\\\\"\\+", "\"+");
						}
						if(line.contains("+\\\"")){
							line = line.replaceAll("\\+\\\\\"", "+\"");
						}
						if(line.contains("\\\"\\\"")){
							line = line.replaceAll("\\\\\"\\\\\"", "\"\"");
						}
						
						System.out.println("html.append(\""+line+"\");");
					}
//				}
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			LOGGER.error("html2java error",e);
		} catch (IOException e) {
			LOGGER.error("html2java error",e);
		}
	}
}
