package com.bridgelabz.fundonotes.note.model;

import java.io.Serializable;
import java.util.Date;

public class UpdateDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String title;
	String description;
	// @ApiModelProperty(hidden = true)
	// Date createdAt;
	// @ApiModelProperty(hidden = true)
	String userId;
	// @ApiModelProperty(hidden = true)
	// Date lastModifiedAt;
	String label;
	String testColor;
	Date setReminder;

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTestColor() {
		return testColor;
	}

	public void setTestColor(String testColor) {
		this.testColor = testColor;
	}

	public Date getSetReminder() {
		return setReminder;
	}

	public void setSetReminder(Date setReminder) {
		this.setReminder = setReminder;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "UpdateDTO [title=" + title + ", description=" + description + ", userId=" + userId + ", label=" + label
				+ ", testColor=" + testColor + ", setReminder=" + setReminder + "]";
	}

}
