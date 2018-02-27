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
package org.axe.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * @author CaiDongyu on 2016/4/8.
 */
public final class StringUtil {
	private StringUtil() {
	}
	
	public static String appendZero(String base,int returnLength){
		StringBuilder buf = new StringBuilder(base);
		while(buf.length() < returnLength){
			buf.append("0");
		}
		String result = buf.toString();
		buf.delete(0, buf.length());
		return result;
	}
	
	public static String aheadZero(String base,int returnLength){
		StringBuilder buf = new StringBuilder(base);
		while(buf.length() < returnLength){
			buf.insert(0, "0");
		}
		String result = buf.toString();
		buf.delete(0, buf.length());
		return result;
	}

	/**
	 * 按照字符顺序排序
	 */
	public static List<String> sortStringSet(Set<String> stringSet) {
		LinkedList<String> sortList = new LinkedList<>();
		if (stringSet != null && stringSet.size() > 0) {
			for (String str : stringSet) {
				if (sortList.size() == 0) {
					sortList.add(str);
					continue;
				}

				int j = 0;
				for (; j < sortList.size(); j++) {
					String sortStr = sortList.get(j);
					if (str.compareTo(sortStr) <= 0) {
						sortList.add(j, str);
						break;
					}
				}
				if (j == sortList.size()) {
					sortList.add(str);
				}
			}
		}
		return sortList;
	}

	/**
	 * 按照字符顺序排序
	 */
	public static List<String> sortStringList(List<String> stringList) {
		LinkedList<String> sortList = new LinkedList<>();
		if (CollectionUtil.isNotEmpty(stringList)) {
			for (String str : stringList) {
				if (sortList.size() == 0) {
					sortList.add(str);
					continue;
				}
				int j = 0;
				for (; j < sortList.size(); j++) {
					String sortStr = sortList.get(j);
					if (str.compareTo(sortStr) <= 0) {
						sortList.add(j, str);
						break;
					}
				}
				if (j == sortList.size()) {
					sortList.add(str);
				}
			}
		}
		return sortList;
	}

	public static List<String> sortStringAry(String[] stringAry) {
		LinkedList<String> sortList = new LinkedList<>();
		if (stringAry != null && stringAry.length > 0) {
			for (String str : stringAry) {
				if (sortList.size() == 0) {
					sortList.add(str);
					continue;
				}
				int j = 0;
				for (; j < sortList.size(); j++) {
					String sortStr = sortList.get(j);
					if (str.compareTo(sortStr) <= 0) {
						sortList.add(j, str);
						break;
					}
				}
				if (j == sortList.size()) {
					sortList.add(str);
				}
			}
		}
		return sortList;
	}

	public static boolean isEmpty(String str) {
		if (str != null) {
			str = str.trim();
		}
		return StringUtils.isEmpty(str);
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static final char UNDERLINE = '_';

	public static String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					sb.append(UNDERLINE);
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == UNDERLINE) {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 获取随机的字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		return getRandomString(length, base);
	}

	public static String getRandomString(int length,String charSource) { // length表示生成字符串的长度
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(charSource.length());
			sb.append(charSource.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// System.out.println(camelToUnderline("SetAbC1d"));
		// System.out.println(underlineToCamel("set_ab_c1d"));
		System.out.println(getRandomString(8));
	}
}
