package com.bridgelabz.fundonotes.note.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class CreateDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String title;
	String description;

	List<Label> label;
	String color;
	Date setReminder;
	boolean archieve;
	boolean pin;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Label> getLabel() {
		return label;
	}

	public void setLabel(List<Label> label) {
		this.label = label;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	

	public Date getSetReminder() {
		return setReminder;
	}

	public void setSetReminder(Date setReminder) {
		this.setReminder = setReminder;
	}
   
	public boolean isArchieve() {
		return archieve;
	}

	public void setArchieve(boolean archieve) {
		this.archieve = archieve;
	}

	public boolean isPin() {
		return pin;
	}

	public void setPin(boolean pin) {
		this.pin = pin;
	}

	@Override
	public String toString() {
		return "CreateDTO [title=" + title + ", description=" + description + ", label=" + label + ", color=" + color
				+ ", setReminder=" + setReminder + ", archieve=" + archieve + ", pin=" + pin + "]";
	}

}
