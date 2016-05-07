package org.jw.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jw.annotation.Table;

/**
 * @Table 数据库 entity 助手类
 * 剥离自DataBaseHelper
 * Created by CaiDongYu on 2016/5/6.
 */
public final class TableHelper {
    
    //#@Table 实体
    private static final Map<String,Class<?>> ENTITY_CLASS_MAP = new HashMap<>();
    
    static{
        //#加载所有@Table指定的Entity类
        Set<Class<?>> entityClassSet = ClassHelper.getClassSetByAnnotation(Table.class);
        for(Class<?> entityClass:entityClassSet){
        	String entityClassSimpleName = entityClass.getSimpleName();
        	if(ENTITY_CLASS_MAP.containsKey(entityClassSimpleName)){
        		throw new RuntimeException("find the same entity class: "+entityClass.getName()+" == "+ENTITY_CLASS_MAP.get(entityClassSimpleName).getName());
        	}
        	ENTITY_CLASS_MAP.put(entityClassSimpleName, entityClass);
        }
    }
    


    /**
     * 返回所有@Table标注的Entity类
     */
    public static Map<String, Class<?>> getEntityClassMap() {
		return ENTITY_CLASS_MAP;
	}
    

    /**
     * 根据Entity获取表名
     * 如果有@Table注解，就取注解值
     * 如果没有，就取类名做表名
     */
    public static String getTableName(Class<?> entityClass) {
    	String tableName = entityClass.getSimpleName();
    	if(entityClass.isAnnotationPresent(Table.class)){
    		tableName = entityClass.getAnnotation(Table.class).value();
    	}
        return tableName;
    }
}
