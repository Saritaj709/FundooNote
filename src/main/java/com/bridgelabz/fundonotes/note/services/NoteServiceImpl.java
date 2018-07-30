package com.bridgelabz.fundonotes.note.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.note.exception.DateException;
import com.bridgelabz.fundonotes.note.exception.LabelAdditionException;
import com.bridgelabz.fundonotes.note.exception.LabelCreationException;
import com.bridgelabz.fundonotes.note.exception.LabelNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NoSuchLabelException;
import com.bridgelabz.fundonotes.note.exception.NoteArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NotePinnedException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnPinnedException;
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UnAuthorizedException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.Label;
import com.bridgelabz.fundonotes.note.model.LabelDTO;
import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewNoteDTO;
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

	/**
	 * 
	 * @param token
	 * @param create
	 * @return Note
	 * @throws NoteNotFoundException
	 * @throws NoteCreationException
	 * @throws UnAuthorizedException
	 * @throws DateException
	 * @throws LabelNotFoundException
	 * @throws NullValueException
	 */
	@Override
	public ViewNoteDTO createNote(String userId, CreateDTO createDto) throws NoteNotFoundException, NoteCreationException,
			UnAuthorizedException, DateException, LabelNotFoundException, NullValueException {

		NoteUtility.validateNoteCreation(createDto);

		Note note = modelMapper.map(createDto, Note.class);

		if (createDto.getColor().equals(null) || createDto.getColor().length() == 0
				|| createDto.getColor().trim().length() == 0) {
			note.setColor(Color);
		}
		if (createDto.getReminder().before(new Date())) {
			throw new DateException("reminder cannot be before current date");

		}

		note.setUserId(userId);
		note.setCreatedAt(new Date());
		note.setLastModifiedAt(new Date());

		for (int i = 0; i < createDto.getLabels().size(); i++) {

			List<Label> labels = labelRepository.findByLabelName(createDto.getLabels().get(i).getLabelName());

			if (labels.isEmpty()) {

			//	labelDto.setLabelName(createDto.getLabel().get(i).getLabelName());
			
				createLabel(userId, createDto.getLabels().get(i).getLabelName());
				List<Label> labels1 = labelRepository.findByLabelName(createDto.getLabels().get(i).getLabelName());
				note.setLabels(labels1);

			}
		}

		noteRepository.save(note);

		ViewNoteDTO viewNoteDto=modelMapper.map(note,ViewNoteDTO.class);
		
		return viewNoteDto;

	}

	/**
	 * 
	 * @param token
	 * @param createLabelDto
	 * @return Label
	 * @throws UnAuthorizedException
	 * @throws NullValueException
	 * @throws NoteNotFoundException
	 */
	@Override
	public LabelDTO createLabel(String userId,String labelName)
			throws UnAuthorizedException, NullValueException, NoteNotFoundException {

		NoteUtility.validateLabelCreation(labelName);

		List<Label> labels = labelRepository.findByLabelNameAndUserId(labelName,userId);

		if (!labels.isEmpty()) {
			throw new LabelCreationException("label with this name already exists");
		}

		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);

		labelRepository.save(label);
		
		LabelDTO labelDto=modelMapper.map(label,LabelDTO.class);

		return labelDto;
	}

	/**
	 * 
	 * @param token
	 * @param labelId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 * @throws LabelAdditionException
	 */
	@Override
	public void addLabel(String userId, String labelId, String noteId)
			throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException, LabelAdditionException {

		Optional<Note> checkNote = noteRepository.findById(noteId);
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		Note note=checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

		List<Label> labels = labelRepository.findByLabelIdAndUserId(labelId,userId);

		if (labels.isEmpty()) {
			throw new NoSuchLabelException("The label with the given id does not exist for given user");
		}

		/*if(!label.get().getUserId().equals(userId)) {
			throw new UnAuthorizedException("the label with given id is not authorized for given user");
		}*/
		

		List<Label> tempList = new LinkedList<>();

		tempList = note.getLabels();

		if (tempList != null) {

			for (int i = 0; i < tempList.size(); i++) {

				if (tempList.get(i).getLabelId().equalsIgnoreCase(labelId)) {
					throw new LabelAdditionException("the label with this labelId already exists");
				}
			}
			tempList.addAll(labels);

			note.setLabels(tempList);
		}

		else {
			note.setLabels(labels);
		}
		noteRepository.save(note);

	}

	/**
	 * 
	 * @return List of Labels
	 * @throws NullValueException
	 */
	@Override
	public List<Label> viewLabels() throws NullValueException {

		List<Label> labelList = labelRepository.findAll();

		if (labelList == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		return labelList;
	}

	/**
	 * 
	 * @param userId
	 * @param labelId
	 * @return List of ViewNoteDTO
	 * @throws LabelNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 */
	@Override
	public List<ViewNoteDTO> viewLabel(String userId, String labelId)
			throws LabelNotFoundException, UnAuthorizedException, NoteNotFoundException {

		Optional<Label> optionalLabel = labelRepository.findByLabelId(labelId);
		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("The label with the given id does not exist");
		}
		
		Label label=optionalLabel.get();

		if (!label.getUserId().equals(userId)) {
			throw new UnAuthorizedException("The user with the given id does not exist");
		}

		ArrayList<Note> noteList = (ArrayList<Note>) noteRepository.findAllByUserId(userId);

		if (noteList == null) {
			throw new NoteNotFoundException("no such note available");
		}

		ArrayList<ViewNoteDTO> viewList = new ArrayList<>();
		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);

			for (int j = 0; j < note.getLabels().size(); j++) {
				if (note.getLabels().get(j).getLabelId().equals(labelId)) {

					ViewNoteDTO viewDto = modelMapper.map(noteList.get(i), ViewNoteDTO.class);

					viewList.add(viewDto);

				}
			}
		}

		return viewList;
	}

	/**
	 * 
	 * @param userId
	 * @param labelName
	 * @throws NoteNotFoundException
	 * @throws LabelNotFoundException
	 * @throws UnAuthorizedException
	 */
	@Override
	public void removeLabel(String userId, String labelId)
			throws NoteNotFoundException, LabelNotFoundException, UnAuthorizedException {

		Optional<Label> optionalLabel = labelRepository.findByLabelId(labelId);
		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("label is not present in list");
		}
        
		Label label=optionalLabel.get();
		
		if (!label.getUserId().equals(userId)) {
			throw new UnAuthorizedException("the user with given id is not found");
		}
		
		List<Note> noteList = noteRepository.findAllByUserId(userId);
		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			for (int j = 0; j < note.getLabels().size(); j++) {
				if (note.getLabels().get(j).getLabelId().equals(labelId)) {
					note.getLabels().remove(i);
					noteRepository.save(note);
				}
			}
		}

		labelRepository.deleteByLabelId(labelId);
	}

	/**
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws LabelNotFoundException
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 */
	@Override
	public void removeLabelFromNote(String userId, String noteId, String labelId)
			throws LabelNotFoundException, NoteNotFoundException, UnAuthorizedException {

		Optional<Label> optionalLabel = labelRepository.findByLabelId(labelId);
		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("the label with given id does not exist");
		}
        
		Label label=optionalLabel.get();
		
		Optional<Note> optionalNote = noteRepository.findByNoteId(noteId);
		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		
		Note note = optionalNote.get();

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("this particular note is not authorized for given user");
		}
		
		if(!label.getUserId().equals(userId)) {
			throw new UnAuthorizedException("this particular label is not authorized for given user");
		}

		for (int i = 0; i < note.getLabels().size(); i++) {
			if (note.getLabels().get(i).getLabelId().equals(labelId)) {
				note.getLabels().remove(i);
				noteRepository.save(note);
			}
		}
	}

	/**
	 * 
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws LabelNotFoundException
	 * @throws UnAuthorizedException
	 */
	@Override
	public void editLabel(String userId, String labelId, String labelName)
			throws LabelNotFoundException, UnAuthorizedException {

		Optional<Label> optionalLabel = labelRepository.findByLabelId(labelId);
		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("label is not present in list");
		}
		
		Label label=optionalLabel.get();

		if (!label.getUserId().equals(userId)) {
			throw new UnAuthorizedException("the user with given id is not found");
		}
		
		List<Note> noteList = noteRepository.findAllByUserId(userId);
		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			for (int j = 0; j < note.getLabels().size(); j++) {
				if (note.getLabels().get(j).getLabelId().equals(labelId)) {
					note.getLabels().get(i).setLabelName(labelName);
					noteRepository.save(note);
				}
			}
		}
		label.setLabelName(labelName);
		labelRepository.save(label);
	}

	/**
	 * 
	 * @param userId
	 * @return LabelDTO
	 * @throws NullValueException 
	 */
	@Override
	public List<LabelDTO> viewUserLabels(String userId) throws NullValueException {

		List<Label> labels=labelRepository.findByUserId(userId);
	 if(labels.isEmpty()) {
		throw new NullValueException("there is no any label for given user"); 
	 }
	 
		List<LabelDTO> userLabels = new LinkedList<>();

	 for(int i=0;i<labels.size();i++) {
		 
	 LabelDTO labelDto=modelMapper.map(labels.get(i),LabelDTO.class);
	 
	 userLabels.add(labelDto);
	 }
	 
	 return userLabels;
	}
	
	/**
	 * 
	 * @param token
	 * @param update
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 */
	@Override
	public void updateNote(String userId, UpdateDTO updateDto)
			throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(updateDto.getNoteId());
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}
	
		Note note=checkNote.get();
		
		if (note.isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

		

		Note note1 = modelMapper.map(updateDto, Note.class);

		note1.setCreatedAt(note.getCreatedAt());
		note1.setLastModifiedAt(new Date());
		note1.setReminder(note.getReminder());
		note1.setColor(note.getColor());
		note1.setUserId(note.getUserId());

		noteRepository.save(note1);
	}

	/**
	 * 
	 * @return List of Trashed Notes
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> viewTrashed() throws NullValueException {

		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		List<ViewNoteDTO> viewList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (noteList.get(index).isTrashed()) {

				ViewNoteDTO viewDto = modelMapper.map(noteList.get(index), ViewNoteDTO.class);
				viewList.add(viewDto);
			}
		}
		return viewList;
	}

	/**
	 * 
	 * @return List of Notes of A Particular user
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> readAllNotes() throws NullValueException {

		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		List<ViewNoteDTO> viewList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				ViewNoteDTO viewDto = modelMapper.map(noteList.get(index), ViewNoteDTO.class);
				viewList.add(viewDto);
			}
		}
		return viewList;
	}

	/**
	 * 
	 * @param userId
	 * @return List of ViewNoteDTO
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> readUserNotes(String userId) throws NullValueException {

		List<Note> noteList = noteRepository.findAllByUserId(userId);
		if (noteList.isEmpty()) {
			throw new NullValueException("the note for given user is empty");
		}

		List<ViewNoteDTO> pinnedList = new LinkedList<>();
		List<ViewNoteDTO> unpinnedList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {
			if (!noteList.get(index).isTrashed()) {
				ViewNoteDTO viewDto = modelMapper.map(noteList.get(index), ViewNoteDTO.class);
				if (noteList.get(index).isPin()) {
					pinnedList.add(viewDto);
				} else {
					unpinnedList.add(viewDto);
				}
			}
		}
		pinnedList.addAll(unpinnedList);
		return pinnedList;
	}

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @return List of ViewNoteDTO
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	@Override
	public ViewNoteDTO findNoteById(String userId, String noteId)
			throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		Note note=checkNote.get();
		
		if (note.isTrashed()) {
			throw new NoteTrashedException("the note with given details are already trashed");
		}
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

		ViewNoteDTO viewDto = modelMapper.map(note, ViewNoteDTO.class);

		return viewDto;
	}

	@Override
	public void deleteNoteForever(String userId, String noteId)
			throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}
        
		Note note=checkNote.get();
		
		if (!note.isTrashed()) {
			throw new UntrashedException("Note is not trashed yet");
		}
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

		noteRepository.deleteByNoteId(noteId);
	}

	/**
	 * 
	 * @param userId
	 * @param color
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 */
	@Override
	public void addColor(String userId, String color, String noteId)
			throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException {
		
		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
         
		Note note=checkNote.get();
		
		if (note.isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

		note.setColor(color);
		noteRepository.save(note);
		
	}

	/**
	 * 
	 * @param token
	 * @param date
	 * @param noteId
	 * @return boolean
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws DateException
	 */
	@Override
	public void addReminder(String userId, Date date, String noteId)
			throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException, DateException {

		if (date.before(new Date())) {
			throw new DateException("reminder cannot be before current date");
		}
		
		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		
		Note note=checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}	
		
		note.setReminder(date);
		
		noteRepository.save(note);

	}

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NullValueException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	@Override
	public void deleteReminder(String userId, String noteId)
			throws NullValueException, UnAuthorizedException, NoteNotFoundException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		
		Note note=checkNote.get();

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

		note.setReminder(null);
		noteRepository.save(note);
	}

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteArchievedException
	 * @throws NoteTrashedException
	 * @throws NoteUnArchievedException
	 */
	@Override
	public void archieveOrUnArchieveNote(String userId, String noteId, boolean choice) throws NoteNotFoundException,
			UnAuthorizedException, NoteArchievedException, NoteTrashedException, NoteUnArchievedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		Note note=checkNote.get();
		
		if (note.isTrashed()) {
			throw new NoteTrashedException("the note with given details is trashed,pls restore first to archieve");
		}
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

			note.setArchieve(choice);

		noteRepository.save(note);
	}

	/**
	 * 
	 * @return List of Archived Notes
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> viewArchieved() throws NullValueException {
		
		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		List<ViewNoteDTO> archieveList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				if (noteList.get(index).isArchieve()) {

					ViewNoteDTO viewDto = modelMapper.map(noteList.get(index), ViewNoteDTO.class);
					archieveList.add(viewDto);
				}
			}
		}
		return archieveList;
	}

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NotePinnedException
	 * @throws NoteTrashedException
	 * @throws NoteUnPinnedException
	 */
	@Override
	public void pinOrUnpinNote(String userId, String noteId, boolean choice) throws NoteNotFoundException,
			UnAuthorizedException, NotePinnedException, NoteTrashedException, NoteUnPinnedException {

		Optional<Note> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}
		
		Note note=checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException("the note with given details is already trashed,pls restore first to pin");
		}
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

			note.setPin(choice);

		noteRepository.save(note);
	}

	/**
	 * 
	 * @return List of Pinned Notes
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> viewPinned() throws NullValueException {

		List<Note> notes = noteRepository.findAll();

		if (notes == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		List<ViewNoteDTO> pinnedList = new LinkedList<>();

		for (int index = 0; index < notes.size(); index++) {

			if (!notes.get(index).isTrashed()) {

				if (notes.get(index).isPin()) {

					ViewNoteDTO viewDto = modelMapper.map(notes.get(index), ViewNoteDTO.class);
					pinnedList.add(viewDto);
				}
			}
		}
		return pinnedList;
	}

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @param choice
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	@Override
	public void deleteOrRestoreNote(String userId, String noteId, boolean choice)
			throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		Note note=checkNote.get();
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}
		
			note.setTrashed(choice);
			
		noteRepository.save(note);
	}

}
