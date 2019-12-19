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
package org.axe.proxy.implement;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.axe.annotation.aop.Aspect;
import org.axe.annotation.ioc.Service;
import org.axe.annotation.persistence.Tns;
import org.axe.bean.persistence.ShardingTableCreateTask;
import org.axe.helper.persistence.DataBaseHelper;
import org.axe.proxy.base.AspectProxy;
import org.axe.util.LogUtil;

/**
 * 事务代理
 * 代理所有 @Service注解的类
 * 只增强 @Tns注解的方法
 * @author CaiDongyu on 2016/4/19.
 */
@Aspect(Service.class)
public final class TransactionAspect extends AspectProxy {
    private static final ThreadLocal<String> TNS_POINT_FLAG_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> SHARDING_TABLE_CREATE_TASK_POINT_FLAG_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Set<ShardingTableCreateTask>> SHARDING_TABLE_CREATE_TASK_HOLDER = new ThreadLocal<>();
    
    /**
     * 添加分片表创建任务
     */
    public static void addShardingTableCreateTask(ShardingTableCreateTask task){
    	Set<ShardingTableCreateTask> set = SHARDING_TABLE_CREATE_TASK_HOLDER.get();
    	if(set  != null){
    		//如果，是经过Service然后调用的Dao，那么这肯定会有值
    		set.add(task);
    	}else{
    		String methodPoint = "Dao";
    		//如果是从别的地方，直接调用的Dao，那么就没有这个值，也不存在事务，那么，可以直接创建此表了
    		SHARDING_TABLE_CREATE_TASK_POINT_FLAG_HOLDER.set(methodPoint);
    		HashSet<ShardingTableCreateTask> taskSet = new HashSet<ShardingTableCreateTask>();
    		taskSet.add(task);
        	SHARDING_TABLE_CREATE_TASK_HOLDER.set(taskSet);
    		doShardingTableCreateTask(methodPoint);
    	}
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
    	String methodPoint = method.toGenericString();
    	
    	//TODO:事务的传播机制
    	//目前比较简单，只是在哪儿打开，就在哪儿关闭，比如方法1通过托管获取并执行了其他Service方法，那么如果要打开事务，就只会打开一次
        String tnsPointFlag = TNS_POINT_FLAG_HOLDER.get();
        //开启事务，条件：1.没有打开过事务。2.方法上有@Tns注解
        if(tnsPointFlag == null && method.isAnnotationPresent(Tns.class)){
            DataBaseHelper.beginTransaction();
            TNS_POINT_FLAG_HOLDER.set(methodPoint);
//            LogUtil.log("begin transaction on point:"+methodPoint);
        }
        
        //分片表的创建机制，在进入第一层事务切面的时候，会添加这个分片表set，供后续Dao切面使用
        String shardingTableCreateTaskPointFlag = SHARDING_TABLE_CREATE_TASK_POINT_FLAG_HOLDER.get();
        if(shardingTableCreateTaskPointFlag == null){
        	SHARDING_TABLE_CREATE_TASK_POINT_FLAG_HOLDER.set(methodPoint);
        	SHARDING_TABLE_CREATE_TASK_HOLDER.set(new HashSet<ShardingTableCreateTask>());
//        	LogUtil.log("init sharding table create task set:"+methodPoint);
        }
        
    }
    
    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
    	String methodPoint = method.toGenericString();
    	
    	String tnsPointFlag = TNS_POINT_FLAG_HOLDER.get();
    	//提交事务关闭连接，条件：1.开启过事务。2.在这个方法上开启了事务。3.方法上有@Tns注解
    	if(tnsPointFlag != null && tnsPointFlag.equals(methodPoint) && method.isAnnotationPresent(Tns.class)){
        	DataBaseHelper.commitTransaction();
        	TNS_POINT_FLAG_HOLDER.remove();
//        	LogUtil.log("commit transaction on point:"+methodPoint);
        }
    	
    	//执行并清空掉分片数据表创建任务
    	doShardingTableCreateTask(methodPoint);
    }
    
    @Override
    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
    	String methodPoint = method.toGenericString();
    	
    	//回退事务
    	String tnsPointFlag = TNS_POINT_FLAG_HOLDER.get();
    	if(tnsPointFlag != null && tnsPointFlag.equals(methodPoint) && method.isAnnotationPresent(Tns.class)){
    		 DataBaseHelper.rollbackTransaction();
             TNS_POINT_FLAG_HOLDER.remove();
//             LogUtil.log("rollback transaction on point:"+methodPoint);
    	}
    	
    	//执行并清空掉分片数据表创建任务
    	doShardingTableCreateTask(methodPoint);
    }
    
    private static void doShardingTableCreateTask(String methodPoint){
        String shardingTableCreateTaskPointFlag = SHARDING_TABLE_CREATE_TASK_POINT_FLAG_HOLDER.get();
    	if(shardingTableCreateTaskPointFlag != null && shardingTableCreateTaskPointFlag.equals(methodPoint)){
    		//分片表与事务无关，事务是否成功都对新增分片表无影响
    		
    		Set<ShardingTableCreateTask> shardingTableCreateSet = SHARDING_TABLE_CREATE_TASK_HOLDER.get();
    		for(ShardingTableCreateTask task:shardingTableCreateSet){
    			try {
    				//执行数据表建表语句
					DataBaseHelper.executeUpdate(task.getCreateDataTableSqlAry(), new Object[]{}, new Class<?>[]{}, task.getDataSourceName());
				
					//执行gt表记录更新语句
					try {
						DataBaseHelper.executeUpdate(task.getUpdateGtTableRecordSql(), new Object[]{}, new Class<?>[]{}, task.getDataSourceName());
					} catch (Exception e) {
						//如果是表重复异常，则不做处理，这种情况
						if(e != null && e.getMessage().toUpperCase().contains("DUPLICATE")){
							//不做处理
						}else{
							throw e;
						}
					}
					
    			} catch (SQLException e) {
	                LogUtil.error("do sharding table ["+task.getTableName()+"]["+task.getShardingFlag()+"] create task failure");
	                LogUtil.error(e);
	                throw new RuntimeException(e);
				}
    		}
    		
    		SHARDING_TABLE_CREATE_TASK_POINT_FLAG_HOLDER.remove();
    		SHARDING_TABLE_CREATE_TASK_HOLDER.remove();
//        	LogUtil.log("clean sharding table create task set on point:"+methodPoint);
        }
    }
    
}
