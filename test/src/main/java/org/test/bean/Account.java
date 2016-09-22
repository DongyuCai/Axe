package org.test.bean;

import java.util.Date;

import org.axe.annotation.persistence.Table;

@Table(value = "iot_user_account",autoCreate = false)
public class Account{
	private long id;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private String loginName = ""; //登录名
	private String password; //密码
	private String mobile = ""; //手机
	private String email = ""; //邮箱
	private Integer allowLoginType; //登录类型
	private Integer accountType ; //账号类型
	private String lastLoginIp = ""; //上次登录IP
	private Date lastLoginTime ; //上次登录时间
	private String salt;
	private Date regeistDate;
	private Integer mobileValidate = 0;//手机验证
	private Integer emailValidate = 0;
	
	private Long enterpriseId; //所属企业ID
	private String payPassword;//支付密码

	private  String wechatOpenId; //微信的openId
	private Date updatePasswordDate; //更新密码日期

	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getAccountType() {
		return accountType;
	}
	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Integer getAllowLoginType() {
		return allowLoginType;
	}
	public void setAllowLoginType(Integer allowLoginType) {
		this.allowLoginType = allowLoginType;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public Date getRegeistDate() {
		return regeistDate;
	}
	public void setRegeistDate(Date regeistDate) {
		this.regeistDate = regeistDate;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getPayPassword() {
		return payPassword;
	}
	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}
	public Integer getMobileValidate() {
		return mobileValidate;
	}
	public void setMobileValidate(Integer mobileValidate) {
		this.mobileValidate = mobileValidate;
	}
	public Integer getEmailValidate() {
		return emailValidate;
	}
	public void setEmailValidate(Integer emailValidate) {
		this.emailValidate = emailValidate;
	}

	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
	}

	public Date getUpdatePasswordDate() {
		return updatePasswordDate;
	}

	public void setUpdatePasswordDate(Date updatePasswordDate) {
		this.updatePasswordDate = updatePasswordDate;
	}
}
