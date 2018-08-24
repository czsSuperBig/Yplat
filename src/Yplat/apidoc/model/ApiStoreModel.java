package Yplat.apidoc.model;

import java.util.List;

public class ApiStoreModel {

	private String describe;
	
	private List<ApiDataModel> reqBody;
	
	private List<ApiDataModel> rspBody;
	
	private boolean enCode;//是否加密

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public List<ApiDataModel> getReqBody() {
		return reqBody;
	}

	public void setReqBody(List<ApiDataModel> reqBody) {
		this.reqBody = reqBody;
	}

	public List<ApiDataModel> getRspBody() {
		return rspBody;
	}

	public void setRspBody(List<ApiDataModel> rspBody) {
		this.rspBody = rspBody;
	}

	public boolean isEnCode() {
		return enCode;
	}

	public void setEnCode(boolean enCode) {
		this.enCode = enCode;
	}
	
	
}
