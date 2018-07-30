package com.bridgelabz.fundonotes.note.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

@Document(collection = "notes")
@Service
public class Note {

	@Id
 	String noteId;
	String title;
	String description;
	Date createdAt;
	String userId;
	Date lastModifiedAt;
	String color="#fff";
	Date reminder=null;
	boolean isTrashed;
	boolean archieve;
	boolean pin;
	List<Label> Labels;

	public Note() {
		super();
	}

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String Color) {
		this.color = Color;
	}

	public Date getReminder() {
		return reminder;
	}

	public void setReminder(Date reminder) {
		this.reminder = reminder;
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

	public void setPin(boolean pinned) {
		this.pin = pinned;
	}

	public List<Label> getLabels() {
		return Labels;
	}

	public void setLabels(List<Label> labels) {
		Labels = labels;
	}

	@Override
	public String toString() {
		return "NoteDTO [noteId=" + noteId + ", title=" + title + ", description=" + description + ", createdAt="
				+ createdAt + ", userId=" + userId + ", lastModifiedAt=" + lastModifiedAt + ", color=" + color
				+ ", reminder=" + reminder + ", isTrashed=" + isTrashed + ", archieve=" + archieve + ", pin="
				+ pin + ", Labels=" + Labels + "]";
	}

}
