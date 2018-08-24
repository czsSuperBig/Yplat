package Yplat.model;

/**
 * 会话信息model 
 */
public class SessionModel {

	private String id;
	
	private String name;
	
	//rsa私钥
	private String rsaPrivateKey;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(String rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}
	
	
	
}
