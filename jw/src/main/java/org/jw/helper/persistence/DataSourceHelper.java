package org.jw.helper.persistence;

import java.util.HashMap;
import java.util.Map;

import org.jw.helper.Helper;
import org.jw.helper.base.ConfigHelper;
import org.jw.interface_.persistence.DataSource;
import org.jw.util.ClassUtil;
import org.jw.util.ReflectionUtil;

/**
 * 数据源 助手类
 * Created by CaiDongYu on 2016年5月20日 下午2:04:11.
 */
public final class DataSourceHelper implements Helper{
	
	/**
	 * 做成Map结构只是为了下一步支持多数据源做准备
	 */
	private static Map<String,DataSource> DATA_SOURCE;
	
	@Override
	public void init() {
		synchronized (this) {
			DATA_SOURCE = new HashMap<>();
			String jdbcDatasource = ConfigHelper.getJdbcDatasource();
			String[] split = jdbcDatasource.split(",");
			for(String dataSourceClassPath:split){
				Class<?> dataSourceClass = ClassUtil.loadClass(dataSourceClassPath, false);
				DataSource dataSource = ReflectionUtil.newInstance(dataSourceClass);
				//TODO:目前还不支持多数据源，默认数据源名称只能是""，即便重写了DataSource.setName()方法也不会有效
				//String name = dataSource.setName();
				String name = "";
				if(DATA_SOURCE.containsKey(name))
					throw new RuntimeException("find the same name DataSource:"+DATA_SOURCE.get(name).getClass()+"==="+dataSource.getClass());
				DATA_SOURCE.put(name, dataSource);
			}
		}
	}
	
	public static Map<String, DataSource> getDataSourceAll() {
		return DATA_SOURCE;
	}
	
	public static DataSource getDataSource(){
		return getDataSource("");
	}
	
	//TODO:按时还不开放此方法
	@Deprecated
	public static DataSource getDataSource(String name){
		return DATA_SOURCE.get(name);
	}
}
