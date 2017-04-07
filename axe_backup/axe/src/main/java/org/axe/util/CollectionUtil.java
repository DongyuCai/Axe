package org.axe.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 集合工具类
 * Created by CaiDongYu on 2016/4/9.
 */
public final class CollectionUtil {
	
	private CollectionUtil() {}
	
    /**
     * 判断 Collection 是否为空
     */
    public static boolean isEmpty(Collection<?> collection){
        return CollectionUtils.isEmpty(collection);
    }

    /**
     * 判断 Collection 是否非空
     */
    public static boolean isNotEmpty(Collection<?> collection){
        return !isEmpty(collection);
    }

    /**
     * 判断 Map 是否为空
     */
    public static boolean isEmpty(Map<?,?> map){
        return MapUtils.isEmpty(map);
    }

    /**
     * 判断 Map 是否非空
     */
    public static boolean isNotEmpty(Map<?,?> map) {
        return !isEmpty(map);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> intersect(List ls, List ls2) {
		List<T> list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.retainAll(ls2);
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> union(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.addAll(ls2);
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> diff(List ls, List ls2) {
		List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.removeAll(ls2);
		return list;
	}
}
