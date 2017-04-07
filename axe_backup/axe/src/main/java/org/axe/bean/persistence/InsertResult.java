package org.axe.bean.persistence;

/**
 * Insert操作结果的包装
 * 因为Insert可能需要返回自增主键
 * 所以包装一下条目和自增主键
 * Created by CaiDongYu on 2016年5月16日 上午9:52:22.
 */
public class InsertResult {

	private int effectedRows;
	private Object generatedKey;
	
	public InsertResult(int effectedRows, Object generatedKey) {
		super();
		this.effectedRows = effectedRows;
		this.generatedKey = generatedKey;
	}

	public int getEffectedRows() {
		return effectedRows;
	}

	public Object getGeneratedKey() {
		return generatedKey;
	}
}
