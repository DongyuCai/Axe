package org.jw.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 数组工具
 * Created by CaiDongYu on 2016/4/11.
 */
public final class ArrayUtil {
	
	private ArrayUtil() {}

    /**
     * 判断空
     */
    public static boolean isEmpty(Object[] ary){
        return ArrayUtils.isEmpty(ary);
    }

    /**
     * 判断非空
     */
    public static boolean isNotEmpty(Object[] ary){
        return !isEmpty(ary);
    }
}
