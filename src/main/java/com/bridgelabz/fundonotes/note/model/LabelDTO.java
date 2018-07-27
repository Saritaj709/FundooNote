package com.bridgelabz.fundonotes.note.model;

import java.io.Serializable;

public class LabelDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String labelId;
	private String labelName;

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

	@Override
	public String toString() {
		return "ViewLabelDTO [labelId=" + labelId + ", labelName=" + labelName + "]";
	}

}
