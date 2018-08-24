package Yplat.model;

public class ResponseHeadModel {

	private String retCode;
	private String retMsg;
	private String retSign;
	
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public String getRetSign() {
		return retSign;
	}
	public void setRetSign(String retSign) {
		this.retSign = retSign;
	}
	
	public ResponseHeadModel(String retCode, String retMsg, String retSign) {
		super();
		this.retCode = retCode;
		this.retMsg = retMsg;
		this.retSign = retSign;
	}
}
