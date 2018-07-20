package com.bridgelabz.fundonotes.note.model;

import java.util.Date;

public class ViewDTO {
	String title;
	String description;
	// @ApiModelProperty(hidden = true)
	Date createdAt;
	// @ApiModelProperty(hidden = true)
	// @ApiModelProperty(hidden = true)
	Date lastModifiedAt;
	String label;
	String testColor;
	Date setReminder;
	boolean isTrashed;

	public ViewDTO() {
		super();
	}

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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
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

	public boolean isTrashed() {
		return isTrashed;
	}

	public void setTrashed(boolean isTrashed) {
		this.isTrashed = isTrashed;
	}

	@Override
	public String toString() {
		return "ViewDTO [title=" + title + ", description=" + description + ", createdAt=" + createdAt
				+ ", lastModifiedAt=" + lastModifiedAt + ", label=" + label + ", testColor=" + testColor
				+ ", setReminder=" + setReminder + ", isTrashed=" + isTrashed + "]";
	}

}
