package com.bridgelabz.fundonotes.note.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewNoteDTO {
	
	private String noteId;
	private String title;
	private String description;
	private Date createdAt;
	private Date lastModifiedAt;
	private List<Label> labels=new ArrayList<>();
	private String color;
	private Date reminder;
	private boolean trashed;
	private boolean archieve;
	private boolean pin;
	private List<UrlMetaData> metaData;
	private List<String> images=new ArrayList<>();

	public ViewNoteDTO() {
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

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
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

	public boolean isTrashed() {
		return trashed;
	}

	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
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

	public List<UrlMetaData> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<UrlMetaData> metaData) {
		this.metaData = metaData;
	}

	public List<String> getImage() {
		return images;
	}

	public void setImage(List<String> image) {
		this.images = image;
	}

	@Override
	public String toString() {
		return "ViewNoteDTO [noteId=" + noteId + ", title=" + title + ", description=" + description + ", createdAt="
				+ createdAt + ", lastModifiedAt=" + lastModifiedAt + ", labels=" + labels + ", color=" + color
				+ ", reminder=" + reminder + ", trashed=" + trashed + ", archieve=" + archieve + ", pin=" + pin
				+ ", metaData=" + metaData + ", image=" + images + "]";
	}

	
}
