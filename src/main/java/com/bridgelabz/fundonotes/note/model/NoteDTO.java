package com.bridgelabz.fundonotes.note.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.note.repository.LabelRepository;

@Document(collection = "notes")
@Service
public class NoteDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
 	String noteId;
	String title;
	String description;
	//@ApiModelProperty(hidden = true)
	Date createdAt;
	//@ApiModelProperty(hidden = true)
	String userId;
	//@ApiModelProperty(hidden = true)
	Date lastModifiedAt;
	String color="white";
	Date setReminder=null;
	boolean isTrashed;
	boolean archieve;
	boolean pin;
	List<LabelDTO> Label;

	public NoteDTO() {
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

	public void setColor(String testColor) {
		this.color = testColor;
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

	public void setPin(boolean pinned) {
		this.pin = pinned;
	}

	public List<LabelDTO> getLabel() {
		return Label;
	}

	public void setLabel(List<LabelDTO> label) {
		Label = label;
	}

	@Override
	public String toString() {
		return "NoteDTO [noteId=" + noteId + ", title=" + title + ", description=" + description + ", createdAt="
				+ createdAt + ", userId=" + userId + ", lastModifiedAt=" + lastModifiedAt + ", color=" + color
				+ ", setReminder=" + setReminder + ", isTrashed=" + isTrashed + ", archieve=" + archieve + ", pin="
				+ pin + ", Label=" + Label + "]";
	}

}
