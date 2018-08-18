package com.bridgelabz.fundonotes.user.model;

public class FacebookUser {

	private String id;
	private String name;
	private String email;
	
	public FacebookUser() {
		
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "FacebookUser [id=" + id + ", name=" + name + ", email=" + email + "]";
	}
	
}
