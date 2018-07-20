package com.bridgelabz.fundonotes.user.model;

public class LoginDTO {
	private String id;
	private String password;
  
	public LoginDTO() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
