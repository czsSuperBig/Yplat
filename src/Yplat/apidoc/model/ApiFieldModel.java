package Yplat.apidoc.model;

public class ApiFieldModel {

    private String describe;//字段的中文描述
	
    private int length;//字段的长度表述
	
    private boolean required;//字段是否必填

	public ApiFieldModel(String describe, int length, boolean required) {
		super();
		this.describe = describe;
		this.length = length;
		this.required = required;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	
    
    
}
