package Yplat.apidoc.model;

import java.util.List;

/**
 * api文档数据模型 
 */
public class ApiDataModel {

	private String fieldName;//字段名
	
	private String classType;//字段类型
	
	private ApiFieldModel apiDescribe;//字段文档描述
	
	private List<ApiDataModel> list;

	public ApiDataModel(String fieldName, ApiFieldModel apiDescribe,String classType) {
		super();
		this.fieldName = fieldName;
		this.apiDescribe = apiDescribe;
		this.classType = classType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public ApiFieldModel getApiDescribe() {
		return apiDescribe;
	}

	public void setApiDescribe(ApiFieldModel apiDescribe) {
		this.apiDescribe = apiDescribe;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public List<ApiDataModel> getList() {
		return list;
	}

	public void setList(List<ApiDataModel> list) {
		this.list = list;
	}
	
	
}
