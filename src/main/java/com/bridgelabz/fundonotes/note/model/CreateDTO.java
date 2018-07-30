package com.bridgelabz.fundonotes.note.model;

import java.sql.Date;
import java.util.List;

public class CreateDTO {

	String title;
	String description;

	List<Label> labels;
	String color;
	Date reminder;
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

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	

	public Date getReminder() {
		return reminder;
	}

	public void setReminder(Date reminder) {
		this.reminder = reminder;
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
		return "CreateDTO [title=" + title + ", description=" + description + ", labels=" + labels + ", color=" + color
				+ ", reminder=" + reminder + ", archieve=" + archieve + ", pin=" + pin + "]";
	}

}
