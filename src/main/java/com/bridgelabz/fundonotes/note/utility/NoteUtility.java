package com.bridgelabz.fundonotes.note.utility;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;

public class NoteUtility {

	public static void validateNoteCreation(CreateDTO createDto) throws NoteCreationException, NullValueException {

		if (createDto.getDescription().length() == 0 && createDto.getTitle().length() == 0) {
			throw new NoteCreationException("Both description and title cannot be null");
		}

		if (createDto.getLabels() == null) {
			throw new NoteCreationException("note label should not be null");
		}

		for (int i = 0; i < createDto.getLabels().size(); i++) {
			if (createDto.getLabels().get(i).getLabelName().equals(null)
					|| createDto.getLabels().get(i).getLabelName().length() == 0
					|| createDto.getLabels().get(i).getLabelName().trim().length() == 0) {
				throw new NullValueException("label name cannot be null,pls enter name for label");
			}
		}
	}

	public static void validateLabelCreation(String labelName) throws NullValueException {

		if (labelName.length() == 0 || labelName.trim().length() == 0) {
			throw new NullValueException("Label name cannot be null");
		}
		if (labelName.length() > 20) {
			throw new NullValueException("Label name cannot be more than 20 characters");
		}
	}
}
