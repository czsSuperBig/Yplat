package com.api.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.api.model.LoginReqModel;
import com.api.model.LoginRspModel;
import com.api.model.UserInfo;

import Yplat.annotation.ApiMapping;
import Yplat.model.SessionModel;
import Yplat.util.encrypt.RSAUtils;

/**
 * 登陆服务 
 */
@Service
public class LoginService {

	@ApiMapping(describe="用户登录接口demo",value="userLogin",checkSession=false,createSession=true)
	public LoginRspModel loginSystem(LoginReqModel reqModel) throws Exception {
		LoginRspModel rspModel = new LoginRspModel();
		rspModel.setLoginTimes(2);
		SessionModel sessionModel = new SessionModel();
		sessionModel.setId(UUID.randomUUID().toString());
		sessionModel.setName(reqModel.getUserName());
		Map<String, String> para = RSAUtils.getKeys();
		sessionModel.setRsaPrivateKey(para.get("privateKey"));
		rspModel.setRsaKey(para.get("publicKey"));
		rspModel.setSessionModel(sessionModel);
		return rspModel;
	}
	
	@ApiMapping(describe="获取用户信息demo",value="getUserInfo")
	public UserInfo getUserInfo(SessionModel sessionModel) {
		UserInfo userInfo = new UserInfo();
		userInfo.setPhoneNo(sessionModel.getId());
		userInfo.setUserName(sessionModel.getName());
		return userInfo;
	}
	
	@ApiMapping(describe="获取用户信息demo",value="getEnCodeUserInfo",isDataEncrypt=true)
	public UserInfo getEnCodeUserInfo(UserInfo userInfo,SessionModel sessionModel) {
		userInfo.setUserName(sessionModel.getName());
		return userInfo;
	}
	
	@ApiMapping(describe="展示用户信息demo（不需会话）",value="displayUserInfo",checkSession=false)
	public UserInfo displayUserInfo(UserInfo userInfo) {
		return userInfo;
	}
}
