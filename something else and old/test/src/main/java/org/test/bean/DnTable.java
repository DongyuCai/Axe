package org.test.bean;

import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Table;

@Table(value="table_@{id}",autoCreate=false)
public class DnTable {
	@Id
	private Long id;
	
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
