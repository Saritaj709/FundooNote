package com.bridgelabz.fundonotes.note.model;

import java.io.Serializable;
import java.util.List;

public class CreateDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String title;
	String description;
	String userId;

	List<LabelDTO> label;
	String color;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<LabelDTO> getLabel() {
		return label;
	}

	public void setLabel(List<LabelDTO> label) {
		this.label = label;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String testColor) {
		this.color = testColor;
	}

	@Override
	public String toString() {
		return "CreateDTO [title=" + title + ", description=" + description + ", userId=" + userId + ", label=" + label
				+ ", testColor=" + color + "]";
	}

}
