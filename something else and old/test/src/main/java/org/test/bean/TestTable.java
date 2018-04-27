package org.test.bean;

import org.axe.annotation.persistence.ColumnDefine;
import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Table;

@Table("Test")
public class TestTable {

	@Id
	private long id;
	@ColumnDefine("char(100) NOT NULL UNIQUE")
	private String name;
	
	private int status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int _getStatus() {
		return status;
	}
	public void _setStatus(int status) {
		this.status = status;
	}
}
