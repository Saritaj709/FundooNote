package com.bridgelabz.fundonotes.note.utility;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;

public class NoteUtility {
	
	public static void validateNoteCreation(CreateDTO note) throws NoteCreationException {
		
		if(note.getDescription()==null&&note.getTitle()==null) {
			throw new NoteCreationException("Both description and title cannot be null");
		}
	
		if(note.getLabel()==null) {
			throw new NoteCreationException("note label should not be null");
		}
		
	}

	public static void validateNoteCreation(CreateLabelDTO createLabelDTO) throws NullEntryException {
		// TODO Auto-generated method stub
		
		if(createLabelDTO.getLabelName()==null) {
			throw new NullEntryException("Label name cannot be null");
		}
		if(createLabelDTO.getUserId()==null) {
			throw new NullEntryException("User id cannot be null");
		}
	}

}  
