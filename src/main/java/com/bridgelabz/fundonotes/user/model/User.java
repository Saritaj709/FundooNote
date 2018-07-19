package com.bridgelabz.fundonotes.user.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Component
@Document(collection = "FundooNotes")
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String email;
	private String firstname;
	private String lastname;
	private String phoneNo;
	private String password;
	private boolean activate;
	public User() {
		super();
	}

	/*public User(String string, boolean b) {
		// TODO Auto-generated constructor stub
		return;
	}*/

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}

	@Override
	public String toString() {
		return "User [email=" + email + ", firstname=" + firstname + ", lastname=" + lastname + ", phoneNo=" + phoneNo
				+ ", password=" + password + ", activate=" + activate + "]";
	}
	
}
