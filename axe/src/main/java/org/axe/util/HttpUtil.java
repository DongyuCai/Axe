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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.axe.constant.CharacterEncoding;

/**
 * Http 帮助类
 * @author CaiDongyu on 2016年6月14日 上午11:26:32.
 */
public final class HttpUtil {
	public static String sendGet(String url) throws Exception {
		return sendGet(url, CharacterEncoding.UTF_8.CHARACTER_ENCODING);
	}
	
	/**
	 * 使用Get方式获取数据
	 * 
	 * @param url
	 * @param charset
	 * @return
	 * @throws Exception 
	 */
	public static String sendGet(String url, String charset) throws Exception {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
//			connection.setRequestProperty("accept", "*/*");
//			connection.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("connection", "close");
//			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			conn.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LogUtil.error("发送GET请求出现异常！"+e.getMessage()+"["+url+"]");
			throw e;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				LogUtil.error("发送GET请求 关闭输入流出现异常！"+e2.getMessage()+"["+url+"]");
			}
		}
		return result;
	}

	public static byte[] downloadGet(String url) throws Exception {
		InputStream in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
//			connection.setRequestProperty("accept", "*/*");
//			connection.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("connection", "close");
//			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			conn.connect();
			// 读取字节
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];    
	        int len = 0;    
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
	        while((len = in.read(buffer)) != -1) {    
	            bos.write(buffer, 0, len);    
	        }    
	        bos.close();    
	        return bos.toByteArray();  
		} catch (Exception e) {
			LogUtil.error("发送GET请求出现异常！"+e.getMessage()+"["+url+"]");
			throw e;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				LogUtil.error("发送GET请求 关闭输入流出现异常！"+e2.getMessage()+"["+url+"]");
			}
		}
	}
	
	public static String sendPostUrl(String url, String param) throws Exception {
		return sendPostUrl(url, param, CharacterEncoding.UTF_8.CHARACTER_ENCODING);
	}
	/**
	 * POST请求，字符串形式数据
	 * 
	 * @param url
	 *            请求地址
	 * @param param
	 *            请求数据
	 * @param charset
	 *            编码方式
	 * @throws Exception 
	 */
	public static String sendPostUrl(String url, String param, String charset) throws Exception {

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
//			conn.setRequestProperty("accept", "*/*");
//			conn.setRequestProperty("connection", "Keep-Alive");
			//#Keep-Alive 长连接，不适用普通的调用，消耗内存倒是其次了已经，在Captain做心跳请求时候，会极大的增加cpu的io消耗，两端都消耗。
			conn.setRequestProperty("connection", "close");
//			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LogUtil.error("发送POST请求出现异常！"+e.getMessage()+"["+url+"]");
			throw e;
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				LogUtil.error("发送POST请求 关闭输出流、输入流出现异常！"+ex.getMessage()+"["+url+"]");
			}
		}
		return result;
	}

	public static String sendPost(String url, Map<String, String> param) throws Exception {
		return sendPost(url, param, CharacterEncoding.UTF_8.CHARACTER_ENCODING);
	}
	/**
	 * POST请求，Map形式数据
	 * 
	 * @param url
	 *            请求地址
	 * @param param
	 *            请求数据
	 * @param charset
	 *            编码方式
	 * @throws Exception 
	 */
	public static String sendPost(String url, Map<String, String> param, String charset) throws Exception {

		StringBuilder buffer = new StringBuilder();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				try {
					if(buffer.length() > 0){
						buffer.append("&");
					}
					buffer.append(entry.getKey()).append("=");
					if(entry.getValue() != null){
						buffer.append(URLEncoder.encode(entry.getValue(), charset));
					}
				} catch (UnsupportedEncodingException e) {
					LogUtil.error(e);
				}
			}
		}

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
//			conn.setRequestProperty("accept", "*/*");
//			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("connection", "close");
//			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(buffer);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LogUtil.error("发送POST请求出现异常！"+e.getMessage()+"["+url+"]");
			throw e;
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				LogUtil.error("发送POST请求 关闭输出流、输入流出现异常！"+ex.getMessage()+"["+url+"]");
			}
		}
		buffer.setLength(0);
		return result;
	}

	public static void main(String[] args) {
		try {
			System.out.println(sendGet("http://192.168.11.116:8080/bigdata/iot-data/coy/A3E1FFE53F1145A3893C37D7AD10BD77/GBOX_SPD_S_T_S_F", "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		};
	}

}
