package com.api.model;

import Yplat.annotation.ApiDescribe;

public class LoginReqModel {

	@ApiDescribe(describe="用户名",length=20)
	private String userName;
	
	@ApiDescribe(describe="密码",length=20)
	private String password;
	
	@ApiDescribe(describe="手机号",length=11)
	private String phoneNo;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	
}
