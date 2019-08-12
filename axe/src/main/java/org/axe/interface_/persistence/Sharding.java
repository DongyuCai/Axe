package org.axe.interface_.persistence;

import org.axe.annotation.persistence.Comment;
import org.axe.annotation.persistence.Table;

/**
 * 数据库分表超类
 * @author CaiDongyu 2018/12/26
 */
public abstract class Sharding implements TableNameEditor{
	
	@Comment("分片标识")
	private Integer shardingFlag;

	public final Integer getShardingFlag() {
		return shardingFlag;
	}

	public final void setShardingFlag(Integer shardingFlag) {
		this.shardingFlag = shardingFlag;
	}

	//单表达到多少数据开始分表
	public abstract int oneTableMaxCount();
	
	@Override
	public final String realTableName()throws Exception {
		String realTableName = null;
		Table table = this.getClass().getAnnotation(Table.class);
		if(table != null){
			realTableName = table.value()+"_sharding_"+shardingFlag;
			return realTableName;
		}else{
			throw new Exception("no @Table annotation,so no real table name");
		}
	}
	
}
