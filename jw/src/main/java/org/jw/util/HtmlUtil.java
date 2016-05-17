package org.jw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class HtmlUtil {
	public static void convertHtmlCode(String htmlFileName){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(htmlFileName)));
			String line = null;
			boolean javaTag = false;
			while((line = reader.readLine()) != null){
				line = StringUtil.isEmpty(line)?null:line.trim();
				if(StringUtil.isNotEmpty(line)){
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
						System.out.println("html.append(\""+line+"\");");
					}
				}
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
