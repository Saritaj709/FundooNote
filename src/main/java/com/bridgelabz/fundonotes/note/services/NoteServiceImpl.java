package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;
import com.bridgelabz.fundonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService{
	
	@Autowired
	NoteRepository noteRepository;
	
	@Autowired
	Token jwtToken;
	
	@Autowired
	ModelMapper modelMapper;
	
	// List<ViewDTO> noteList=new LinkedList<>();

	@Override
	public ViewDTO createNote(String token,CreateDTO create) throws NoteNotFoundException, NoteCreationException, UserNotFoundException {
		
		String parsed=jwtToken.parseJwtToken(token);	
		String userId=create.getUserId();
		
		if(!parsed.equals(userId)) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
	
		NoteUtility.validateNoteCreation(create);
		
		NoteDTO noteDto=modelMapper.map(create,NoteDTO.class);
		
			noteDto.setUserId(parsed);
			noteDto.setCreatedAt(new Date());
			noteDto.setSetReminder(null);
			noteDto.setLastModifiedAt(new Date());
			noteRepository.save(noteDto);
			
			ViewDTO viewNote=modelMapper.map(create,ViewDTO.class);
			viewNote.setCreatedAt(new Date());
			viewNote.setSetReminder(null);
			viewNote.setLastModifiedAt(new Date());
			
			//noteRepository.save(viewNote);
			
			 //noteList.add(viewNote);
			 
			return viewNote;
	}

	@Override
	public void updateNote(String token,UpdateDTO update,String noteId) throws NoteNotFoundException, UserNotFoundException {

		String parsed=jwtToken.parseJwtToken(token);	
		String userId=update.getUserId();
		
		if(!parsed.equals(userId)) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(update.getNoteId());
	    if(!checkNote.isPresent()) {
	    	throw new NoteNotFoundException("The note with given id does not exist");
	    }
	    
	   NoteDTO note=modelMapper.map(update,NoteDTO.class);
	   note.setCreatedAt(new Date());
		note.setLastModifiedAt(new Date());
		noteRepository.save(note);
	}

	@Override
	public boolean moveNoteToTrash(String token,String userId,String noteId) throws NoteNotFoundException, UserNotFoundException {
	
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
	    if(!checkNote.isPresent()) {
	    	throw new NoteNotFoundException("The note with given id does not exist");
	    }
	    
	    checkNote.get().setTrashed(true);
	    noteRepository.save(checkNote.get());
	    return true;
	}

	@Override
	public List<NoteDTO> readAllNotes() throws NullEntryException {
		
		List<NoteDTO> noteList = noteRepository.findAll();
		
		if(noteList==null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}
		return noteList;
	}

	@Override
	public List<NoteDTO> readNotes() throws NullEntryException {
		List<NoteDTO> noteList=noteRepository.findAll();
		if(noteList==null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}
		
		return noteList;
	}
	
	
	@Override
	public ViewDTO findNoteById(String token,String noteId,String userId) throws UserNotFoundException, NoteNotFoundException {
		
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
	
		ViewDTO viewNote=modelMapper.map(checkNote,ViewDTO.class);
		viewNote.setCreatedAt(new Date());
		viewNote.setSetReminder(null);
		viewNote.setLastModifiedAt(new Date());
		
		return viewNote;
	}
	
	@Override
	public void deleteNote(String token,String userId,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException {
	
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		if(!moveNoteToTrash(token,userId,noteId)) {
			throw new UntrashedException("Note is not trashed yet");		
		}
		
		Optional<NoteDTO>checkNote=noteRepository.findByNoteId(noteId);
		System.out.println(checkNote);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}
		
		noteRepository.deleteById(noteId);	
	}

	@Override
	public boolean addReminder(String token,String userId,Date date,String noteId) throws UserNotFoundException, NoteNotFoundException {
        
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		//findNoteById(token,userId,noteId);
		
		//Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		 checkNote.get().setSetReminder(date);
		 noteRepository.save(checkNote.get());
		return true;
		
	}

	@Override
	public void deleteReminder(String token,String userId,Date date,String noteId) throws NullEntryException, UserNotFoundException, NoteNotFoundException {
       
		String parsed=jwtToken.parseJwtToken(token);	
		
		if(!parsed.equals(userId)) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		if(!addReminder(token,userId,date,noteId)) {
			throw new NullEntryException("There is no reminder for the note yet");
		}
		//Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		 checkNote.get().setSetReminder(null);
		 noteRepository.save(checkNote.get());
	}

}
