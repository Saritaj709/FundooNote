package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService{
	
	@Autowired
	NoteRepository noteRepository;
	
	@Autowired
	Token jwtToken;
	
	@Autowired
	ModelMapper modelMapper;

	@Override
	public void createNote(String token,CreateDTO create) throws NoteCreationException {
		
		String parsed=jwtToken.parseJwtToken(token);	
		String userId=create.getUserId();
		if(!parsed.equals(userId)) {
	    	throw new NoteCreationException("Please enter valid token to match your account");
	    }
	
		NoteUtility.validateNoteCreation(create);
		
		NoteDTO noteDto=modelMapper.map(create,NoteDTO.class);
		
			noteDto.setUserId(parsed);
			noteDto.setCreatedAt(new Date());
			noteDto.setSetReminder(null);
			noteDto.setLastModifiedAt(new Date());
			noteRepository.save(noteDto);
	}

	@Override
	public void updateNote(String token,UpdateDTO update,String noteId) throws NoteCreationException {

		String parsed=jwtToken.parseJwtToken(token);	
		String userId=update.getUserId();
		
		if(!parsed.equals(userId)) {
	    	throw new NoteCreationException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(update.getNoteId());
	    if(!checkNote.isPresent()) {
	    	throw new NoteCreationException("The note with given id does not exist");
	    }
	    
	   NoteDTO note=modelMapper.map(update,NoteDTO.class);
	   note.setCreatedAt(new Date());
		note.setLastModifiedAt(new Date());
		noteRepository.save(note);
	}

	@Override
	public boolean moveNoteToTrash(String token,String userId,String noteId) throws NoteCreationException {
	
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new NoteCreationException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
	    if(!checkNote.isPresent()) {
	    	throw new NoteCreationException("The note with given id does not exist");
	    }
	    
	    checkNote.get().setTrashed(true);
	    noteRepository.save(checkNote.get());
	    return true;
	}

	@Override
	public List<NoteDTO> readAllNotes() throws NoteCreationException {
		
		List<NoteDTO> noteList = noteRepository.findAll();
		
		if(noteList==null) {
			throw new NoteCreationException("There is no any details stored in note yet");
		}
		return noteList;
	}

	@Override
	public boolean findNoteById(String token,String noteId,String userId) throws NoteCreationException {
		
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new NoteCreationException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteCreationException("the note with given id does not exist");
		}
		noteRepository.findById(noteId);
		return true;
	}
	@Override
	public void deleteNote(String token,String userId,String noteId) throws NoteCreationException {
	
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new NoteCreationException("Please enter valid token to match your account");
	    }
		
		if(!moveNoteToTrash(token,userId,noteId)) {
			throw new NoteCreationException("Note is not trashed yet");		
		}
		
		Optional<NoteDTO>checkNote=noteRepository.findByNoteId(noteId);
		System.out.println(checkNote);
		
		if(!checkNote.isPresent()) {
			throw new NoteCreationException("The given note does not exist");
		}
		
		noteRepository.deleteById(noteId);	
	}

	@Override
	public boolean addReminder(String token,String userId,Date date,String noteId) throws NoteCreationException {
String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new NoteCreationException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteCreationException("the note with given id does not exist");
		}
		//findNoteById(token,userId,noteId);
		
		//Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		 checkNote.get().setSetReminder(date);
		 noteRepository.save(checkNote.get());
		return true;
		
	}

	@Override
	public void deleteReminder(String token,String userId,Date date,String noteId) throws NoteCreationException {
String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new NoteCreationException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteCreationException("the note with given id does not exist");
		}
		if(!addReminder(token,userId,date,noteId)) {
			throw new NoteCreationException("There is no reminder for the note yet");
		}
		//Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		 checkNote.get().setSetReminder(null);
		 noteRepository.save(checkNote.get());
	}

}
