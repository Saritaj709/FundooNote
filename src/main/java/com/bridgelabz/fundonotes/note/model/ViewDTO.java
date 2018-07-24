package com.bridgelabz.fundonotes.note.model;

import java.util.Date;
import java.util.List;

public class ViewDTO {
	String title;
	String description;
	// @ApiModelProperty(hidden = true)
	Date createdAt;
	// @ApiModelProperty(hidden = true)
	// @ApiModelProperty(hidden = true)
	Date lastModifiedAt;
	List<LabelDTO> label;
	String testColor;
	Date setReminder;
	boolean isTrashed;
	boolean archieve;
	boolean pin;

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

	public List<LabelDTO> getLabel() {
		return label;
	}

	public void setLabel(List<LabelDTO> label) {
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
		return "ViewDTO [title=" + title + ", description=" + description + ", createdAt=" + createdAt
				+ ", lastModifiedAt=" + lastModifiedAt + ", label=" + label + ", testColor=" + testColor
				+ ", setReminder=" + setReminder + ", isTrashed=" + isTrashed + ", archieve=" + archieve + ", pin="
				+ pin + "]";
	}

}
