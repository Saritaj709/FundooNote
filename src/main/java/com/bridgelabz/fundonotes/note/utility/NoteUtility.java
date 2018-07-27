package com.bridgelabz.fundonotes.note.utility;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;

public class NoteUtility {
	
	public static void validateNoteCreation(CreateDTO createDto) throws NoteCreationException, NullEntryException {
		
		if(createDto.getDescription()==null&&createDto.getTitle()==null) {
			throw new NoteCreationException("Both description and title cannot be null");
		}
	
		if(createDto.getLabel()==null) {
			throw new NoteCreationException("note label should not be null");
		}
		
		for(int i=0;i<createDto.getLabel().size();i++) {
			if(createDto.getLabel().get(i).getLabelName().equals(null)||createDto.getLabel().get(i).getLabelName().length()==0||createDto.getLabel().get(i).getLabelName().trim().length()==0) {
				throw new NullEntryException("label name cannot be null,pls enter name for label");
			}
		}
	}

	public static void validateLabelCreation(CreateLabelDTO createLabelDTO) throws NullEntryException {
		// TODO Auto-generated method stub
		
		if(createLabelDTO.getLabelName()==null||createLabelDTO.getLabelName().length()==0||createLabelDTO.getLabelName().trim().length()==0) {
			throw new NullEntryException("Label name cannot be null");
		}
		if(createLabelDTO.getUserId()==null||createLabelDTO.getUserId().length()==0||createLabelDTO.getUserId().trim().length()==0) {
			throw new NullEntryException("User id cannot be null");
		}
	}

}  
