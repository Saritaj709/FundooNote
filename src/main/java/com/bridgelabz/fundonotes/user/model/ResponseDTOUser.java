package com.bridgelabz.fundonotes.user.model;

public class ResponseDTOUser {
	private String message;
	private int status;

	public ResponseDTOUser() {
		super();
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
