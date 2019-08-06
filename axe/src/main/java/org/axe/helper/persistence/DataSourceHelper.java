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

import org.axe.annotation.persistence.DataSource;
import org.axe.helper.base.ConfigHelper;
import org.axe.helper.ioc.ClassHelper;
import org.axe.interface_.base.Helper;
import org.axe.interface_.persistence.BaseDataSource;
import org.axe.util.ReflectionUtil;
import org.axe.util.StringUtil;

/**
 * 数据源 助手类
 * @author CaiDongyu on 2016年5月20日 下午2:04:11.
 */
public final class DataSourceHelper implements Helper{
	
	private static String DEFAULT_DATASOURCE_NAME = null;
	/**
	 * 做成Map结构只是为了下一步支持多数据源做准备
	 */
	private static Map<String,BaseDataSource> DATA_SOURCE;
	
	@Override
	public void init() throws Exception{
		synchronized (this) {
			Set<Class<?>> dataSourceFactoryClassSet = ClassHelper.getClassSetBySuper(BaseDataSource.class);
			Map<String,Class<?>> dataSourceFactoryClassMap = new HashMap<>();
			for(Class<?> dataSourceFactoryClass:dataSourceFactoryClassSet){
				if(dataSourceFactoryClass.isAnnotationPresent(DataSource.class)){
					String dataSourceName = dataSourceFactoryClass.getAnnotation(DataSource.class).value();
					if(StringUtil.isEmpty(dataSourceName))
						throw new Exception("find the empty name DataSource:"+dataSourceFactoryClass);
					if(dataSourceFactoryClassMap.containsKey(dataSourceName))
						throw new Exception("find the same name DataSource:"+DATA_SOURCE.get(dataSourceName).getClass()+"==="+dataSourceFactoryClass);
					dataSourceFactoryClassMap.put(dataSourceName, dataSourceFactoryClass);
				}
			}
			
			DATA_SOURCE = new HashMap<>();
			String jdbcDatasource = ConfigHelper.getJdbcDatasource();
			String[] split = jdbcDatasource.split(",");
			
			for(String dataSourceNameConfig:split){
				//默认数据源取得是配置数据源列表中的第一个
				if(StringUtil.isNotEmpty(dataSourceNameConfig)){
					if(DEFAULT_DATASOURCE_NAME == null){
						DEFAULT_DATASOURCE_NAME = dataSourceNameConfig;
					}
					if(dataSourceFactoryClassMap.containsKey(dataSourceNameConfig)){
						BaseDataSource dataSource = ReflectionUtil.newInstance(dataSourceFactoryClassMap.get(dataSourceNameConfig));
						DATA_SOURCE.put(dataSourceNameConfig, dataSource);
					}else{
						throw new Exception("can not find DataSource:"+dataSourceNameConfig);
					}
				}
			}
		}
	}
	
	public static Map<String, BaseDataSource> getDataSourceAll() {
		return DATA_SOURCE;
	}
	
	public static boolean isMySql(String dataSourceName){
		if(DATA_SOURCE.containsKey(dataSourceName)){
			return DATA_SOURCE.get(dataSourceName).setJdbcDriver().toUpperCase().indexOf("MYSQL") >= 0;
		}
		return false;
	}
	public static boolean isOracle(String dataSourceName){
		if(DATA_SOURCE.containsKey(dataSourceName)){
			return DATA_SOURCE.get(dataSourceName).setJdbcDriver().toUpperCase().indexOf("ORACLE") >= 0;
		}
		return false;
	}
	
	/*public static DataSource getDataSource(){
		return getDataSource("");
	}*/
	
	//TODO(OK):暂时还不开放此方法，未来准备支持多数据源
	//这个方法暂时框架还没用到，方法比较关键，所以注释掉
	//@Deprecated
	/*public static BaseDataSource getDataSource(String name){
		return DATA_SOURCE.get(name);
	}*/
	
	public static String getDefaultDataSourceName(){
		return DEFAULT_DATASOURCE_NAME;
	}
	
	public static BaseDataSource getDefaultDataSource(){
		/*if(DATA_SOURCE.size() >= 1){
			for(String dataSourceName:DATA_SOURCE.keySet()){
				if(dataSourceName.equals(DEFAULT_DATASOURCE_NAME)){
					return DATA_SOURCE.get(dataSourceName);
				}
			}
		}*/
		return DATA_SOURCE.get(DEFAULT_DATASOURCE_NAME);
	}

	@Override
	public void onStartUp() throws Exception {}
}
