package org.test.bean;

import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Table;

@Table("banner")
public class Banner {
	@Id
	private long id;
	//标题
	private String title;
	private String titleStyle;
	//描述
	private String desc;
	private String descStyle;
	//url
	private String url;
	//img背景图
	private long fileId;
	//banner div样式
	private String bannerStyle;
	
	private int status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTitleStyle() {
		return titleStyle;
	}
	public void setTitleStyle(String titleStyle) {
		this.titleStyle = titleStyle;
	}
	public String getDescStyle() {
		return descStyle;
	}
	public void setDescStyle(String descStyle) {
		this.descStyle = descStyle;
	}
	public String getBannerStyle() {
		return bannerStyle;
	}
	public void setBannerStyle(String bannerStyle) {
		this.bannerStyle = bannerStyle;
	}
	
}
