package com.bridgelabz.fundonotes.note.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/*@Document(collection="labels")
@Service*/
@Document(indexName = "labeldb", type = "label")
public class Label {

	@Id
	private String labelId;
	private String labelName;
	private String userId;
	private Date labelCreatedAt;

	public Label() {
		super();
	}

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getLabelCreatedAt() {
		return labelCreatedAt;
	}

	public void setLabelCreatedAt(Date labelCreatedAt) {
		this.labelCreatedAt = labelCreatedAt;
	}

	@Override
	public String toString() {
		return "Label [labelId=" + labelId + ", labelName=" + labelName + ", userId=" + userId + ", createdAt="
				+ labelCreatedAt + "]";
	}
}
