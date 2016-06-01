package org.axe.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by CaiDongYu on 2016/4/8.
 */
public final class StringUtil {
	private StringUtil() {}

	/**
	 * 按照字符顺序排序
	 */
	public static List<String> sortStringSet(Set<String> stringSet){
		LinkedList<String> sortList = new LinkedList<>();
		if(stringSet != null && stringSet.size() > 0){
			for(String str:stringSet){
				if(sortList.size() == 0){
					sortList.add(str);
					continue;
				}
				
				int j=0;
				for(;j<sortList.size();j++){
					String sortStr = sortList.get(j);
					if(str.compareTo(sortStr) <= 0){
						sortList.add(j, str);
						break;
					}
				}
				if(j == sortList.size()){
					sortList.add(str);
				}
			}
		}
		return sortList;
	}
	
	/**
	 * 按照字符顺序排序
	 */
	public static List<String> sortStringList(List<String> stringList){
		LinkedList<String> sortList = new LinkedList<>();
		if(CollectionUtil.isNotEmpty(stringList)){
			for(String str:stringList){
				if(sortList.size() == 0){
					sortList.add(str);
					continue;
				}
				int j=0;
				for(;j<sortList.size();j++){
					String sortStr = sortList.get(j);
					if(str.compareTo(sortStr) <= 0){
						sortList.add(j, str);
						break;
					}
				}
				if(j == sortList.size()){
					sortList.add(str);
				}
			}
		}
		return sortList;
	}
	
	public static List<String> sortStringAry(String[] stringAry){
		LinkedList<String> sortList = new LinkedList<>();
		if(stringAry != null && stringAry.length > 0){
			for(String str:stringAry){
				if(sortList.size() == 0){
					sortList.add(str);
					continue;
				}
				int j=0;
				for(;j<sortList.size();j++){
					String sortStr = sortList.get(j);
					if(str.compareTo(sortStr) <= 0){
						sortList.add(j, str);
						break;
					}
				}
				if(j == sortList.size()){
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
				if(i > 0){
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

	public static void main(String[] args) {
		System.out.println(camelToUnderline("SetAbC1d"));
		System.out.println(underlineToCamel("set_ab_c1d"));
	}
}
