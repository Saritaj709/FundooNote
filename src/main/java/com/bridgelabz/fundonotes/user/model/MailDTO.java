package com.bridgelabz.fundonotes.user.model;

import java.io.Serializable;

public class MailDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String to;
	private String subject;
	private String text;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "MailDTO [to=" + to + ", subject=" + subject + ", text=" + text + "]";
	}

}
