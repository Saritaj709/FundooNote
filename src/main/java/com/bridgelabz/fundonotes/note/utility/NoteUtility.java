package com.bridgelabz.fundonotes.note.utility;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;

public class NoteUtility {
	
	public static void validateNoteCreation(CreateDTO note) throws NoteCreationException {
		
		if(note.getDescription()==null&&note.getTitle()==null) {
			throw new NoteCreationException("Both description and title cannot be null");
		}
		/*if(note.getColor()==null) {
			throw new NoteCreationException("test color should not be null");
		}*/
		if(note.getLabel()==null) {
			throw new NoteCreationException("note label should not be null");
		}
		if(note.getUserId()==null) {
			throw new NoteCreationException("User id cannot be null");
		}
	}

}  
