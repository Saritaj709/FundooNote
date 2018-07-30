package com.bridgelabz.fundonotes.note.model;

public class Response {
	
	private String message;
	private int status;

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

	@Override
	public String toString() {
		return "NoteResponseDTO [message=" + message + ", status=" + status + "]";
	}

}
