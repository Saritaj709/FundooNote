package com.bridgelabz.fundonotes.note.model;

import java.util.Date;

public class LabelDTO {

	private String labelId;
	private String labelName;
	private Date labelCreatedAt;

	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public Date getLabelCreatedAt() {
		return labelCreatedAt;
	}

	public void setLabelCreatedAt(Date labelCreatedAt) {
		this.labelCreatedAt = labelCreatedAt;
	}

	@Override
	public String toString() {
		return "LabelDTO [labelId=" + labelId + ", labelName=" + labelName + ", createdAt=" + labelCreatedAt + "]";
	}

}
