package org.test_jw.bean;

import org.jw.annotation.persistence.Id;
import org.jw.annotation.persistence.Table;

@Table("Test")
public class TestTable {

	@Id
	private long id;
	private String name;
	
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
}
