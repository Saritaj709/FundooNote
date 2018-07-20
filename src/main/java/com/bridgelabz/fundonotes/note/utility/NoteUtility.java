package com.bridgelabz.fundonotes.note.utility;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;

public class NoteUtility {
	
	/*private final static String EMAIL = "^\\w+@\\w+\\..{2,3}(.{2,3})?$";
	private final static String DATE="/^\\d{2}([./-])\\d{2}\\1\\d{4}$/";*/
	
	public static void validateNoteCreation(CreateDTO note) throws NoteCreationException {
		
		/*if(note.getDescription().length()<3) {
			throw new NoteCreationException("description should be at least of 3 characters");
		}*/
		/*if(note.getSetReminder().compareTo(note.getCreatedAt())<0) {
			throw new NoteCreationException("invalid reminder date,it should be more than current date");
		}*/
		if(note.getDescription()==null&&note.getTitle()==null) {
			throw new NoteCreationException("Both description and title cannot be null");
		}
		if(note.getTestColor()==null) {
			throw new NoteCreationException("test color should not be null");
		}
		if(note.getLabel()==null) {
			throw new NoteCreationException("note label should not be null");
		}
		if(note.getUserId()==null) {
			throw new NoteCreationException("User id cannot be null");
		}
	}

}  
