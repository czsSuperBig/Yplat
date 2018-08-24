package com.api.model;

import Yplat.annotation.ApiDescribe;

public class UserInfo {

	@ApiDescribe(describe="用户账号")
	private String userName;
	
	@ApiDescribe(describe="用户手机号",required=false)
	private String phoneNo;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	
}
