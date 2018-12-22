package com.ty.modules.msg.entity;

public class Status {
	
	private ResponseCode code;
	private String message;
	
	public Status(ResponseCode code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public static Status buildStatus(ResponseCode code, String message) {
		return new Status(code, message);
	}

	public String getCode() {
		return code.toString();
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}


