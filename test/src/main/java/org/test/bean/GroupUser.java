package org.test.bean;

import java.util.Date;

import org.axe.annotation.persistence.Table;
@Table("iot_user_group2user")
public class GroupUser extends IdEntity {

	private Long userId;
	private Long groupId;
	private Long creator;
	private Date createTime;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getCreator() {
		return creator;
	}
	public void setCreator(Long creator) {
		this.creator = creator;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
}
