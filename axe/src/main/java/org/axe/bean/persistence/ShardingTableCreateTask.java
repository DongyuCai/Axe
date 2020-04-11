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
package org.axe.bean.persistence;

/**
 * 分片表的新增分片任务
 */
public final class ShardingTableCreateTask {
	/*public static void main(String[] args) {
		Set<ShardingTableCreateTask> set = new HashSet<>();
		ShardingTableCreateTask task1 = new ShardingTableCreateTask();
		task1.setTableName("abc");
		task1.setShardingFlag(1);
		ShardingTableCreateTask task2 = new ShardingTableCreateTask();
		task2.setTableName("abc");
		task2.setShardingFlag(2);
		set.add(task1);
		set.add(task2);
		for(ShardingTableCreateTask t:set){
			LogUtil.log(t.getTableName());
			LogUtil.log(t.getShardingFlag());
		}
	}*/
	
	private String dataSourceName;
	
	//表名，原始表名，不带分片b标识
	private String tableName;
	
	//分片标识
	private int shardingFlag;
	
	//建表语句
	private String[] createDataTableSqlAry;
	
	//gt表更新语句
	private String updateGtTableRecordSql;

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getShardingFlag() {
		return shardingFlag;
	}

	public void setShardingFlag(int shardingFlag) {
		this.shardingFlag = shardingFlag;
	}

	public String[] getCreateDataTableSqlAry() {
		return createDataTableSqlAry;
	}

	public void setCreateDataTableSqlAry(String[] createDataTableSqlAry) {
		this.createDataTableSqlAry = createDataTableSqlAry;
	}

	public String getUpdateGtTableRecordSql() {
		return updateGtTableRecordSql;
	}

	public void setUpdateGtTableRecordSql(String updateGtTableRecordSql) {
		this.updateGtTableRecordSql = updateGtTableRecordSql;
	}

	@Override
	public boolean equals(Object otherObj) {
		if(otherObj instanceof ShardingTableCreateTask){
			ShardingTableCreateTask other = (ShardingTableCreateTask)otherObj;
			return other.getTableName().equals(this.tableName);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.tableName.hashCode();
	}
}
