package com.bridgelabz.fundonotes.note.model;

import java.io.Serializable;

public class CreateLabelDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String labelName;
	private String userId;
	private String noteId;

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	@Override
	public String toString() {
		return "CreateLabelDTO [labelName=" + labelName + ", userId=" + userId + ", noteId=" + noteId + "]";
	}

}
