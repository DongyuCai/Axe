package org.jw.helper.mvc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jw.helper.ioc.ClassHelper;
import org.jw.interface_.mvc.Filter;
import org.jw.util.CollectionUtil;
import org.jw.util.ReflectionUtil;

/**
 * Filter 助手 ，类似BeanHelper管理加载所有bean一样，这里是托管Filter
 * 之所以和BeanHelper分开，因为BeanHelper托管了Controller和Service
 * Filter不应该放一起
 * Created by CaiDongYu on 2016/4/9.
 */
public class FilterHelper {
	private static final List<Filter> FILTER_LIST = new ArrayList<>();//保证顺序
    static {
        Set<Class<?>> filterClassSet = ClassHelper.getClassSetBySuper(Filter.class);
        List<Filter> filterSortedList = new LinkedList<>();
        for(Class<?> filterClass:filterClassSet){
        	Filter filter = ReflectionUtil.newInstance(filterClass);
        	filter.init();//初始化Filter
        	
        	//排序比较，按顺序插入到Filter链里
        	if(CollectionUtil.isEmpty(filterSortedList)){
        		filterSortedList.add(filter);
        	}else{
        		int index = 0;
        		for(Filter filter_:filterSortedList){
        			if(filter.setLevel() < filter_.setLevel()){
        				filterSortedList.add(index, filter);
        				break;
        			}else{
        				index++;
        			}
        		}
        		if(index == filterSortedList.size()){
        			filterSortedList.add(filter);
        		}
        	}
        	
        }
        
        FILTER_LIST.addAll(filterSortedList);
    }
    
    public static List<Filter> getSortedFilterList(){
    	return FILTER_LIST;
    }
    
    
}
