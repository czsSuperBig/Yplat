package Yplat.model;

public class ResponseModel {
	
	private ResponseHeadModel head;
	
	private Object body;
	
	public ResponseHeadModel getHead() {
		return head;
	}

	public void setHead(ResponseHeadModel head) {
		this.head = head;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
	
	public ResponseModel(ResponseHeadModel head, Object body) {
		super();
		this.head = head;
		this.body = body;
	}

}
