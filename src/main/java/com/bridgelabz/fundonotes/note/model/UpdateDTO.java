package com.bridgelabz.fundonotes.note.model;

import java.io.Serializable;

public class UpdateDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String title;
	String description;
	String noteId;
	String label;

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

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	@Override
	public String toString() {
		return "UpdateDTO [title=" + title + ", description=" + description + ", noteId="
				+ noteId + ", label=" + label + "]";
	}

}
