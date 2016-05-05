package org.test_jw.bean;

import java.util.Date;

import org.jw.annotation.Table;

@Table("iot_export")
public class Export extends IdEntity{
	//任务名
	private String name;
	//状态:0:正在执行,1:已完成,2:中止(线程断了,可能中途服务重启等原因)
	private int status;
	//进度:1~100
	private int process;
	//剩余秒数
	private long stillWaitSec;
	//识别码(加上创建人后唯一，这个识别码要开发人员根据各个导出功能安排生成策略，以保证任务不重复)
	private String code;
	//创建人
	private long accountId;
	
	private int downloadTimes;//下载次数
	
	private Date createTime;
	private Date lastDownloadTime;//最近一次下载时间

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getProcess() {
		return process;
	}

	public void setProcess(int process) {
		this.process = process;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public int getDownloadTimes() {
		return downloadTimes;
	}

	public void setDownloadTimes(int downloadTimes) {
		this.downloadTimes = downloadTimes;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastDownloadTime() {
		return lastDownloadTime;
	}

	public void setLastDownloadTime(Date lastDownloadTime) {
		this.lastDownloadTime = lastDownloadTime;
	}

	public long getStillWaitSec() {
		return stillWaitSec;
	}

	public void setStillWaitSec(long stillWaitSec) {
		this.stillWaitSec = stillWaitSec;
	}
}
