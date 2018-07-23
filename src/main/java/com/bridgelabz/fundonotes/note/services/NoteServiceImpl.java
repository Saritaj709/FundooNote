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
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;
import com.bridgelabz.fundonotes.note.repository.NoteRepository;
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
	public void createNote(String token,CreateDTO createDto) throws NoteNotFoundException, NoteCreationException, UserNotFoundException {
		
		NoteUtility.validateNoteCreation(createDto);
		
		String id=jwtToken.parseJwtToken(token);	
		String userId=createDto.getUserId();
		
		if(!id.equals(userId)) {
	    	throw new UserNotFoundException("The user with given id does not exist");
	    }
		
		NoteDTO noteDto=modelMapper.map(createDto,NoteDTO.class);
		
		if(createDto.getColor().equals(null)||createDto.getColor().length()==0||createDto.getColor().trim().length()==0) {
			noteDto.setColor("white");
		}
			noteDto.setUserId(id);
			noteDto.setCreatedAt(new Date());
			noteDto.setSetReminder(null);
			noteDto.setLastModifiedAt(new Date());
		
			noteRepository.save(noteDto);
		
	}

	@Override
	public void updateNote(String token,UpdateDTO updateDto) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {

		String id=jwtToken.parseJwtToken(token);	
		
		Optional<NoteDTO>checkNote=noteRepository.findById(updateDto.getNoteId());
	    if(!checkNote.isPresent()) {
	    	throw new NoteNotFoundException("The note with given id does not exist");
	    }
	    
	    if(!id.equals(checkNote.get().getUserId())) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
	    
	    if(checkNote.get().isTrashed()) {
	    	throw new NoteTrashedException("this note no longer exists");
	    }
	    
	   NoteDTO note=modelMapper.map(updateDto,NoteDTO.class);
	   
	    note.setCreatedAt(checkNote.get().getCreatedAt());
		note.setLastModifiedAt(new Date());
	    note.setSetReminder(null);
	    note.setColor(checkNote.get().getColor());
	    note.setUserId(checkNote.get().getUserId());
	    
		noteRepository.save(note);
	}

	@Override
	public boolean trashNote(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
	    if(!checkNote.isPresent()) {
	    	throw new NoteNotFoundException("The note with given id does not exist");
	    }
	    
	    String id=jwtToken.parseJwtToken(token);	
	    
	  /* if(!id.equals(checkNote.get().getUserId())) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
	    */
	    if(checkNote.get().isTrashed()) {
	    	throw new NoteTrashedException("the note with given details is already trashed");
	    }
	    
	    checkNote.get().setTrashed(true);
	    noteRepository.save(checkNote.get());
	    
	    return true;
	}

	@Override
	public void restoreNote(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {
	
		String id=jwtToken.parseJwtToken(token);	
		
		Optional<NoteDTO>checkNote=noteRepository.findByNoteId(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}
		
	/*	if(!id.equals(checkNote.get().getUserId())) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }*/
		
		if(!checkNote.get().isTrashed()) {
			throw new UntrashedException("Note is already restored,it is not trashed yet");		
		}
		
		checkNote.get().setTrashed(false);
		noteRepository.save(checkNote.get());
		
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
	public List<ViewDTO> readNotes() throws NullEntryException {
		
		List<NoteDTO> noteList=noteRepository.findAll();
		
		if(noteList==null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}
				
		List<ViewDTO> viewList=new LinkedList<>();
		
		for(int index=0;index<noteList.size();index++) {
			
		  if(!noteList.get(index).isTrashed()){
			  
		    	ViewDTO viewDto=new ViewDTO();
			viewDto.setCreatedAt(noteList.get(index).getCreatedAt());
			viewDto.setDescription(noteList.get(index).getDescription());
			viewDto.setTitle(noteList.get(index).getTitle());
			viewDto.setSetReminder(noteList.get(index).getSetReminder());
			viewDto.setTestColor(noteList.get(index).getColor());
			viewDto.setTrashed(noteList.get(index).isTrashed());
			viewDto.setLabel(noteList.get(index).getLabel());
			viewDto.setLastModifiedAt(noteList.get(index).getLastModifiedAt());
			viewList.add(viewDto);
		    }
		}
		return viewList;
	}
	
	
	@Override
	public ViewDTO findNoteById(String token,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {
		
		String id=jwtToken.parseJwtToken(token);	
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		
		if(!id.equals(checkNote.get().getUserId())) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		if(checkNote.get().isTrashed()) {
			throw new NoteTrashedException("the note with given details are already trashed");
		}
		
		ViewDTO viewDto=modelMapper.map(checkNote,ViewDTO.class);
		
		viewDto.setCreatedAt(checkNote.get().getCreatedAt());
		viewDto.setDescription(checkNote.get().getDescription());
		viewDto.setTitle(checkNote.get().getTitle());
		viewDto.setSetReminder(checkNote.get().getSetReminder());
		viewDto.setTestColor(checkNote.get().getColor());
		viewDto.setTrashed(checkNote.get().isTrashed());
		viewDto.setLabel(checkNote.get().getLabel());
		viewDto.setLastModifiedAt(checkNote.get().getLastModifiedAt());
		
		return viewDto;
	}
	
	@Override
	public void deleteNoteForever(String token,String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {
	
		Optional<NoteDTO>checkNote=noteRepository.findByNoteId(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}
		
		String id=jwtToken.parseJwtToken(token);
		
		 if(!id.equals(checkNote.get().getUserId())) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		if(!trashNote(token,noteId)) {
			throw new UntrashedException("Note is not trashed yet");		
		}
		
		noteRepository.deleteByNoteId(noteId);	
	}

	@Override
	public boolean addReminder(String token,Date date,String noteId) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {
        
		String id=jwtToken.parseJwtToken(token);	
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		
		if(!id.equals(checkNote.get().getUserId())) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
		
		if(checkNote.get().isTrashed()) {
	    	throw new NoteTrashedException("this note no longer exists");
	    }
		
	/*	if(checkNote.get().getSetReminder()!=null) {
			throw new NoRemiderToSetException("the given note is already having some reminder");
		}*/
		
		 checkNote.get().setSetReminder(date);
		 noteRepository.save(checkNote.get());
		return true;
		
	}

	@Override
	public void deleteReminder(String token,String noteId) throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException {
       
		String id=jwtToken.parseJwtToken(token);	
		
		Optional<NoteDTO>checkNote=noteRepository.findById(noteId);
		
		if(!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		
		if(!id.equals(checkNote.get().getUserId())) {
	    	throw new UserNotFoundException("Please enter valid token to match your account");
	    }
			
		if(checkNote.get().getSetReminder()==null) {
			throw new NullEntryException("There is no reminder for the note yet");
		}
		
		 checkNote.get().setSetReminder(null);
		 noteRepository.save(checkNote.get());
	}

}
