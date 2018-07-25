package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.note.exception.LabelCreationException;
import com.bridgelabz.fundonotes.note.exception.NoSuchLabelException;
import com.bridgelabz.fundonotes.note.exception.NoteArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NotePinnedException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;
import com.bridgelabz.fundonotes.note.model.Label;
import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.repository.LabelRepository;
import com.bridgelabz.fundonotes.note.repository.NoteRepository;
import com.bridgelabz.fundonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	NoteRepository noteRepository;

	@Autowired
	Token jwtToken;

	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	LabelRepository labelRepository;
	
	@Value("${Color}")
	String Color;

	@Override
	public Note createNote(String userId, CreateDTO createDto)
			throws NoteNotFoundException, NoteCreationException, UserNotFoundException {

		NoteUtility.validateNoteCreation(createDto);

		Note noteDto = modelMapper.map(createDto, Note.class);

		if (createDto.getColor().equals(null) || createDto.getColor().length() == 0
				|| createDto.getColor().trim().length() == 0) {
			noteDto.setColor(Color);
		}
		noteDto.setUserId(userId);
		noteDto.setCreatedAt(new Date());
		noteDto.setLastModifiedAt(new Date());

		noteRepository.save(noteDto);
		
		return noteDto;

	}

	@Override
	public Label createLabel(String userId, CreateLabelDTO createLabelDto)
			throws UserNotFoundException, NullEntryException, NoteNotFoundException {
		// TODO Auto-generated method stub

		NoteUtility.validateNoteCreation(createLabelDto);

		if (!userId.equals(createLabelDto.getUserId())) {
			throw new UserNotFoundException("The user with given id does not exist");
		}
		
		Optional<Label> labelList=labelRepository.findByLabelName(createLabelDto.getLabelName());
		if(labelList.isPresent()) {
			throw new LabelCreationException("label with this name already exists");
		}
		
		Label labelDto=modelMapper.map(createLabelDto,Label.class);
		
		labelRepository.save(labelDto);
		
		return labelDto;
	}

	@Override
	public void addLabel(String userId, String labelName, String noteId) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {
		// TODO Auto-generated method stub

		Optional<Note> checkNote = noteRepository.findById(noteId);
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		Optional<Label> labelList = labelRepository.findByLabelName(labelName);
		
		if(!labelList.isPresent()) {
			throw new NoSuchLabelException("The label with the given name does not exist");
		}
		
		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}
	
		List<Label> tempList=checkNote.get().getLabel(); // to get labels which are already present in note
		tempList.add(labelList.get());
	
		checkNote.get().setLabel(tempList);
		noteRepository.save(checkNote.get());
	
	}
	

	@Override
	public List<Label> viewLabels() throws NullEntryException {
		// TODO Auto-generated method stub
		List<Label> labelList = labelRepository.findAll();

		if (labelList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}
		
		return labelList;		
	}
	
	@Override
	public void editOrRemoveLabel(String userId,Label labelDto,String choice) throws Exception {
		// TODO Auto-generated method stub

		if (!userId.equals(labelDto.getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		Optional<Label> label=labelRepository.findByLabelId(labelDto.getLabelId());
		
		if(!label.isPresent()) {
			throw new NoSuchLabelException("The label with the given id does not exist");
		}
		
		Optional<Note> checkNote=noteRepository.findByUserId(labelDto.getUserId());
		List<Label> labelList=checkNote.get().getLabel();
		for(int index=0;index<=labelList.size();index++) {
			if(label.isPresent()) {
				checkNote.get().getLabel();
				//checkNote.get().setLabel(labelRepository.deleteById(labelDto.getLabelId()););
				
			}
		}
		
		if(choice.equalsIgnoreCase("edit")) {
	
			labelDto.setLabelName(labelDto.getLabelName());
			labelDto.setLabelId(label.get().getLabelId());
			labelDto.setUserId(label.get().getUserId());
			labelRepository.save(labelDto);
		}
		
		if(choice.equalsIgnoreCase("delete")) {
			
			labelRepository.deleteByLabelId(labelDto.getLabelId());
		}
	}

	
	@Override
	public void updateNote(String userId, UpdateDTO updateDto)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(updateDto.getNoteId());
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}

		Note note = modelMapper.map(updateDto, Note.class);

		note.setCreatedAt(checkNote.get().getCreatedAt());
		note.setLastModifiedAt(new Date());
		note.setSetReminder(checkNote.get().getSetReminder());
		note.setColor(checkNote.get().getColor());
		note.setUserId(checkNote.get().getUserId());

		noteRepository.save(note);
	}

	@Override
	public List<Note> viewTrashed() throws NullEntryException {
		// TODO Auto-generated method stub

		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<Note> viewList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (noteList.get(index).isTrashed()) {

				Note viewDto=modelMapper.map(noteList.get(index),Note.class);
				viewList.add(viewDto);
			}
		}
		return viewList;
	}

	@Override
	public List<Note> readAllNotes() throws NullEntryException {

		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}
		return noteList;
	}

	@Override
	public Note findNoteById(String userId, String noteId)
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("the note with given details are already trashed");
		}

		Note viewDto = modelMapper.map(checkNote.get(),Note.class);

		return viewDto;
	}

	@Override
	public void deleteNoteForever(String userId, String noteId)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (!checkNote.get().isTrashed()) {
			throw new UntrashedException("Note is not trashed yet");
		}

		noteRepository.deleteByNoteId(noteId);
	}

	@Override
	public boolean addReminder(String userId, Date date, String noteId)
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}

		checkNote.get().setSetReminder(date);
		noteRepository.save(checkNote.get());
		return true;

	}

	@Override
	public void deleteReminder(String userId, String noteId)
			throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().getSetReminder() == null) {
			throw new NullEntryException("There is no reminder for the note yet");
		}

		checkNote.get().setSetReminder(null);
		noteRepository.save(checkNote.get());
	}

	@Override
	public void archieveNote(String userId, String noteId)
			throws NoteNotFoundException, UserNotFoundException, NoteArchievedException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("the note with given details is trashed,pls restore first to archieve");
		}

		if (checkNote.get().isArchieve()) {
			throw new NoteArchievedException("the note with given details is already archieved");
		}

		checkNote.get().setArchieve(true);
		noteRepository.save(checkNote.get());
	}

	@Override
	public List<Note> viewArchieved() throws NullEntryException {
		// TODO Auto-generated method stub
		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<Note> archieveList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				if (noteList.get(index).isArchieve()) {
					
					Note viewDto=modelMapper.map(noteList.get(index),Note.class);
					archieveList.add(viewDto);	
				}
			}
		}
		return archieveList;
	}

	@Override
	public void pinNote(String userId, String noteId)
			throws NoteNotFoundException, UserNotFoundException, NotePinnedException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("the note with given details is already trashed,pls restore first to pin");
		}

		if (checkNote.get().isArchieve()) {
			throw new NotePinnedException("the note with given details is already pinned");
		}

		checkNote.get().setPin(true);
		noteRepository.save(checkNote.get());
	}

	@Override
	public List<Note> viewPinned() throws NullEntryException {
		// TODO Auto-generated method stub
		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<Note> pinnedList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				if (noteList.get(index).isPin()) {

					/*ViewDTO viewDto = new ViewDTO();
					viewDto.setCreatedAt(noteList.get(index).getCreatedAt());
					viewDto.setDescription(noteList.get(index).getDescription());
					viewDto.setTitle(noteList.get(index).getTitle());
					viewDto.setSetReminder(noteList.get(index).getSetReminder());
					viewDto.setTestColor(noteList.get(index).getColor());
					viewDto.setTrashed(noteList.get(index).isTrashed());
					viewDto.setArchieve(noteList.get(index).isArchieve());
					viewDto.setPin(noteList.get(index).isPin());
					viewDto.setLabel(noteList.get(index).getLabel());
					viewDto.setLastModifiedAt(noteList.get(index).getLastModifiedAt());
					viewList.add(viewDto);
					return viewList;*/
					
					Note viewDto=modelMapper.map(noteList.get(index),Note.class);
					pinnedList.add(viewDto);	
				}
			}
		}
		return pinnedList;
	}

	@Override
	public void deleteOrRestoreNote(String userId, String noteId, String choice) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {
		// TODO Auto-generated method stub
		
		Optional<Note> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}
		
		if(choice.equalsIgnoreCase("restore")) {

		if (!checkNote.get().isTrashed()) {
			throw new UntrashedException("Note is already restored,it is not trashed yet");
		}

		checkNote.get().setTrashed(false);
		noteRepository.save(checkNote.get());
		}
		
		if(choice.equalsIgnoreCase("delete")) {
			
			if (checkNote.get().isTrashed()) {
				throw new NoteTrashedException("the note with given details is already trashed");
			}

			checkNote.get().setTrashed(true);
			noteRepository.save(checkNote.get());

		}
	}
	
}
