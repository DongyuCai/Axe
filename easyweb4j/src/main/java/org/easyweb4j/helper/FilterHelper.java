package org.easyweb4j.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.easyweb4j.filter.Filter;
import org.easyweb4j.util.ReflectionUtil;

/**
 * Filter 助手 ，类似BeanHelper管理加载所有bean一样，这里是托管Filter
 * 之所以和BeanHelper分开，因为BeanHelper托管了Controller和Service
 * Filter不应该放一起
 * Created by CaiDongYu on 2016/4/9.
 */
public class FilterHelper {
	private static final List<Filter> FILTER_LIST = new ArrayList<>();//保证顺序
    static {
        Set<Class<?>> filterClassSet = ClassHelper.getFilterClassSet();
        for(Class<?> filterClass:filterClassSet){
            Object obj = ReflectionUtil.newInstance(filterClass);
            FILTER_LIST.add((Filter)obj);
        }
        FILTER_LIST.sort((f1,f2)->{if(((Filter)f1).setLevel() > ((Filter)f2).setLevel()) return 1;return -1;});
    }
    
    public static List<Filter> getSortedFilterList(){
    	return FILTER_LIST;
    }
    
    
}
