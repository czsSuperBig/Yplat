package com.api.model;

import Yplat.annotation.ApiDescribe;
import Yplat.model.SessionModel;

public class LoginRspModel {

	@ApiDescribe(describe="rsa公钥")
	private String rsaKey;
	
	@ApiDescribe(describe="当天登陆的次数",required=false)
	private int loginTimes;
	
	private SessionModel sessionModel;
	

	public SessionModel getSessionModel() {
		return sessionModel;
	}

	public void setSessionModel(SessionModel sessionModel) {
		this.sessionModel = sessionModel;
	}

	public String getRsaKey() {
		return rsaKey;
	}

	public void setRsaKey(String rsaKey) {
		this.rsaKey = rsaKey;
	}

	public int getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(int loginTimes) {
		this.loginTimes = loginTimes;
	}
	
	
}
