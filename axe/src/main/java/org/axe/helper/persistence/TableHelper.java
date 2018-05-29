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
package org.axe.helper.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.axe.annotation.persistence.Table;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;

/**
 * @Table 数据库 entity 助手类 解析表名，字段名 
 * 剥离自DataBaseHelper 
 * @author CaiDongyu on 2016/5/6. 
 */
public final class TableHelper implements Helper{

	// #@Table 实体
	private static Map<String, Class<?>> ENTITY_CLASS_MAP;

	@Override
	public void init() {
		synchronized (this) {
			ENTITY_CLASS_MAP = new HashMap<>();
			// #加载所有@Table指定的Entity类
			// select a.* from ClassA    这句sql里的ClassA，就是entityClassMap里的key
			Set<Class<?>> entityClassSet = ClassHelper.getClassSetByAnnotation(Table.class);
			for (Class<?> entityClass : entityClassSet) {
				String entityClassSimpleName = entityClass.getSimpleName();
				if (ENTITY_CLASS_MAP.containsKey(entityClassSimpleName)) {
					throw new RuntimeException("find the same entity class: " + entityClass.getName() + " == "
							+ ENTITY_CLASS_MAP.get(entityClassSimpleName).getName());
				}
				ENTITY_CLASS_MAP.put(entityClassSimpleName, entityClass);
			}
			
		}
	}

	/**
	 * 返回所有@Table标注的Entity类
	 */
	public static Map<String, Class<?>> getEntityClassMap() {
		return ENTITY_CLASS_MAP;
	}

	/**
	 * 根据Entity获取表名 如果有@Table注解，就取注解值 如果没有，就取类名做表名
	 */
	public static String getTableName(Class<?> entityClass) {
		if (entityClass.isAnnotationPresent(Table.class)) {
			return entityClass.getAnnotation(Table.class).value();
		}else{
			throw new RuntimeException(entityClass.getName() + " is not a table entity class,no @Table annotation is found on it");
		}
	}
	

    public static <T> String getTableDataSourceName(Class<T> entityClass){
    	if(entityClass != null && entityClass.isAnnotationPresent(Table.class)){
    		//1.如果注明了数据源名称，就找到相应数据源返回
    		String dataSourceName = entityClass.getAnnotation(Table.class).dataSource();
    		return dataSourceName.equals("")?DataSourceHelper.getDefaultDataSourceName():dataSourceName;
    	}else{
    		//2.如果没有注明所属数据源，返回默认数据源
    		return DataSourceHelper.getDefaultDataSourceName();
    	}
    }
	
	public static boolean isTableAutoCreate(Class<?> entityClass){
		if (entityClass.isAnnotationPresent(Table.class)) {
			return entityClass.getAnnotation(Table.class).autoCreate();
		}else{
			throw new RuntimeException(entityClass.getName() + " is not a table entity class,no @Table annotation is found on it");
		}
	}

	@Override
	public void onStartUp() throws Exception {}
}
