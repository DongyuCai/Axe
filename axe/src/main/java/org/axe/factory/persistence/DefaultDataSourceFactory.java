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
package org.axe.factory.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.axe.annotation.persistence.DataSource;
import org.axe.helper.base.ConfigHelper;
import org.axe.interface_.persistence.BaseDataSource;
import org.axe.util.LogUtil;
import org.axe.util.StringUtil;

@DataSource("default-datasource")
public final class DefaultDataSourceFactory implements BaseDataSource{
	
    //#数据库
    private final String DRIVER;
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private final Integer CONNECTION_POLL_SIZE;
    
    //数据源缓存
    Map<Connection,Boolean> CONNECTION_POOL = new HashMap<>();
	
	public DefaultDataSourceFactory() {
        //#初始化jdbc配置
        DRIVER = setJdbcDriver();
        URL = setJdbcUrl();
        USERNAME = setJdbcUserName();
        PASSWORD = setJdbcPassword();
        CONNECTION_POLL_SIZE = setJdbcConnectionPoolSize();
        
        do{
        	if(StringUtil.isEmpty(DRIVER)) break;
        	if(StringUtil.isEmpty(URL)) break;
        	if(StringUtil.isEmpty(USERNAME)) break;
//        	if(StringUtil.isEmpty(PASSWORD)) break;
        	//么有配置的话，默认不会初始化数据源
        	init();
        }while(false);
	}
	
	private void init() {
        
        try {
            Class.forName(DRIVER);
        } catch (Exception e) {
            LogUtil.error("jdbc driver : " + DRIVER);
            LogUtil.error("jdbc url : " + URL);
            LogUtil.error("jdbc username : " + USERNAME);
            LogUtil.error("jdbc password : " + PASSWORD);
            LogUtil.error(e);
        }
        
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection con = null;
		//查看是否有空闲连接
		synchronized (CONNECTION_POOL) {
			if(!CONNECTION_POOL.isEmpty()){
				for(Connection one:CONNECTION_POOL.keySet()){
					if(!CONNECTION_POOL.get(one)){
						con = one;
						CONNECTION_POOL.put(con, true);
						LogUtil.log(Thread.currentThread().getName()+"-复用连接，当前pool size:"+CONNECTION_POOL.size());
						break;
					}
				}
			}
		}
		
		if(con == null){
			//如果缓存没有达到上限，则补充一个连接进去
			synchronized (CONNECTION_POOL) {
				if(CONNECTION_POOL.size() < CONNECTION_POLL_SIZE){
					con = DriverManager.getConnection(URL,USERNAME,PASSWORD);
					CONNECTION_POOL.put(con, true);
					LogUtil.log(Thread.currentThread().getName()+"-打开连接，当前pool size:"+CONNECTION_POOL.size());
				}
			}
		}
		if(con == null){
			LogUtil.log(Thread.currentThread().getName()+"-等待");
			//说明缓存达到上限，不能再补充了
			try {
				Thread.sleep(20);//休息20ms后再次尝试获取缓存连接，有可能有新连接释放
			} catch (Exception e) {}
			return getConnection();
		}else{
			return con;
		}
	}
	
	@Override
	public void closeConnection(Connection con) throws SQLException{
		synchronized (CONNECTION_POOL) {
			if(!CONNECTION_POOL.isEmpty()){
				CONNECTION_POOL.put(con, false);
				LogUtil.log(Thread.currentThread().getName()+"-关闭连接，当前pool size:"+CONNECTION_POOL.size());
			}
		}
	}
	
	@Override
	public String setJdbcDriver() {
		return ConfigHelper.getJdbcDriver();
	}

	@Override
	public String setJdbcUrl() {
		return ConfigHelper.getJdbcUrl();
	}

	@Override
	public String setJdbcUserName() {
		return ConfigHelper.getJdbcUsername();
	}

	@Override
	public String setJdbcPassword() {
		return ConfigHelper.getJdbcPassword() == null?"":ConfigHelper.getJdbcPassword();
	}

	@Override
	public int setJdbcConnectionPoolSize() {
		return ConfigHelper.getJdbcConnectionPoolSize();
	}

	@Override
	public boolean tns() {
		return true;
	}
}
