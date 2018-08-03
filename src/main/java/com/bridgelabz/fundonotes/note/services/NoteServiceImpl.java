package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.note.exception.DateException;
import com.bridgelabz.fundonotes.note.exception.LabelAdditionException;
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
import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewNoteDTO;
import com.bridgelabz.fundonotes.note.repository.ElasticRepositoryForLabel;
import com.bridgelabz.fundonotes.note.repository.ElasticRepositoryForNote;
import com.bridgelabz.fundonotes.note.repository.NoteRepository;
import com.bridgelabz.fundonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteRepository noteRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ElasticRepositoryForLabel labelElasticRepository;
	
	@Autowired
	private ElasticRepositoryForNote noteElasticRepository;
	
	@Autowired
	private LabelService labelService;
	
	@Autowired
	private Environment environment;

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
			note.setColor(environment.getProperty("Color"));
		}
		
		if (createDto.getReminder().before(new Date())) {
			throw new DateException("reminder cannot be before current date");

		}

		note.setUserId(userId);
		note.setCreatedAt(new Date());
		note.setLastModifiedAt(new Date());

		for (int i = 0; i < createDto.getLabels().size(); i++) {

			List<Label> labels = labelElasticRepository.findByLabelNameAndUserId(createDto.getLabels().get(i).getLabelName(),userId);
			
			if (labels.isEmpty()) {
			
				labelService.createLabel(userId, createDto.getLabels().get(i).getLabelName());
				
				List<Label> labels1 = labelElasticRepository.findByLabelNameAndUserId(createDto.getLabels().get(i).getLabelName(),userId);
				
				note.setLabels(labels1);

			}
		}

		noteRepository.save(note);
        
		noteElasticRepository.save(note);
		
		ViewNoteDTO viewNoteDto=modelMapper.map(note,ViewNoteDTO.class);
		
		return viewNoteDto;

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

		Optional<Note> checkNote = noteElasticRepository.findById(noteId);
		
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note=checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		//List<Label> labels = labelRepository.findByLabelIdAndUserId(labelId,userId);

		List<Label> labels = labelElasticRepository.findByLabelIdAndUserId(labelId,userId);
		
		if (labels.isEmpty()) {
			throw new NoSuchLabelException(environment.getProperty("NoSuchLabelException"));
		}
	
		List<Label> tempList = new LinkedList<>();

		tempList = note.getLabels();

		if (tempList != null) {

			for (int i = 0; i < tempList.size(); i++) {

				if (tempList.get(i).getLabelId().equals(labelId)) {
					throw new LabelAdditionException(environment.getProperty("LabelAdditionException"));
				}
			}
			tempList.addAll(labels);

			note.setLabels(tempList);
		}

		else {
			note.setLabels(labels);
		}
		noteRepository.save(note);
		
		noteElasticRepository.save(note);

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

		//Optional<Label> optionalLabel = labelRepository.findByLabelId(labelId);
		
		Optional<Label> optionalLabel = labelElasticRepository.findByLabelId(labelId);
		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("the label with given id does not exist");
		}
        
		Label label=optionalLabel.get();
		
		//Optional<Note> optionalNote = noteRepository.findByNoteId(noteId);
		Optional<Note> optionalNote = noteElasticRepository.findByNoteId(noteId);
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
				noteElasticRepository.save(note);
			}
		}
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
	//Optional<Note> checkNote = noteElasticRepository.findById(updateDto.getNoteId());
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}
	
		Note note=checkNote.get();
		
		if (note.isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your 				account");
		}

		

		Note note1 = modelMapper.map(updateDto, Note.class);

		note1.setCreatedAt(note.getCreatedAt());
		note1.setLastModifiedAt(new Date());
		note1.setReminder(note.getReminder());
		note1.setColor(note.getColor());
		note1.setUserId(note.getUserId());

		noteRepository.save(note1);
		noteElasticRepository.save(note1);
	}

	/**
	 * 
	 * @return List of Trashed Notes
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> viewTrashed(String userId) throws NullValueException {

		//List<Note> noteList = noteRepository.findAll();
		List<Note> noteList = noteElasticRepository.findAllByUserId(userId);

		if (noteList == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		return noteList.stream().filter(noteStream-> noteStream.isTrashed()).map(filterNote-> modelMapper.map(filterNote, ViewNoteDTO.class)).collect(Collectors.toList());
	}

	/**
	 * 
	 * @return List of Notes of A Particular user
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> readAllNotes() throws NullValueException {

		//List<Note> noteList = noteRepository.findAll();
		List<Note> noteList = (List<Note>) noteElasticRepository.findAll();

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

		//List<Note> noteList = noteRepository.findAllByUserId(userId);
		List<Note> noteList = noteElasticRepository.findAllByUserId(userId);
		if (noteList.isEmpty()) {
			throw new NullValueException("the note for given user is empty");
		}
		
		List<ViewNoteDTO> pin=noteList.stream().filter(noteStream->!noteStream.isTrashed()&&noteStream.isPin()).map(filterNote->modelMapper.map(filterNote,ViewNoteDTO.class)).collect(Collectors.toList());
		List<ViewNoteDTO> unPin=noteList.stream().filter(noteStream->!noteStream.isTrashed() && !noteStream.isPin()).map(filterNote->modelMapper.map(filterNote,ViewNoteDTO.class)).collect(Collectors.toList());
	/*pin.addAll(unPin);
		return pin;*/
	return	Stream.concat(pin.stream(), unPin.stream())
		   .collect(Collectors.toList());
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

		//Optional<Note> checkNote = noteRepository.findById(noteId);
		
		Optional<Note> checkNote = noteElasticRepository.findById(noteId);

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

		//Optional<Note> checkNote = noteRepository.findByNoteId(noteId);
		
		Optional<Note> checkNote = noteElasticRepository.findByNoteId(noteId);

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
		noteElasticRepository.deleteByNoteId(noteId);
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
		
		//Optional<Note> checkNote = noteRepository.findById(noteId);
		
		Optional<Note> checkNote = noteElasticRepository.findById(noteId);

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
		noteElasticRepository.save(note);
		
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
		
	//	Optional<Note> checkNote = noteRepository.findById(noteId);
		
		Optional<Note> checkNote = noteElasticRepository.findById(noteId);

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
		noteElasticRepository.save(note);

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

		//Optional<Note> checkNote = noteRepository.findById(noteId);
		Optional<Note> checkNote = noteElasticRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}
		
		Note note=checkNote.get();

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}

		note.setReminder(null);
		noteRepository.save(note);
		noteElasticRepository.save(note);
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

		//Optional<Note> checkNote = noteRepository.findById(noteId);
		
		Optional<Note> checkNote = noteElasticRepository.findById(noteId);
		
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
		noteElasticRepository.save(note);
	}

	/**
	 * 
	 * @return List of Archived Notes
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> viewArchieved(String userId) throws NullValueException {
		
		//List<Note> noteList = noteRepository.findAll();
		List<Note> noteList = noteElasticRepository.findAllByUserId(userId);

		if (noteList == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		return noteList.stream().filter(noteStream->!noteStream.isTrashed() &&noteStream.isArchieve()).map(filterNote->modelMapper.map(filterNote,ViewNoteDTO.class)).collect(Collectors.toList());
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

		//Optional<Note> checkNote = noteRepository.findById(noteId);
		Optional<Note> checkNote = noteElasticRepository.findById(noteId);

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
		noteElasticRepository.save(note);
	}

	/**
	 * 
	 * @return List of Pinned Notes
	 * @throws NullValueException
	 */
	@Override
	public List<ViewNoteDTO> viewPinned(String userId) throws NullValueException {

		//List<Note> notes = noteRepository.findAll();
		List<Note> notes = noteElasticRepository.findAllByUserId(userId);

		if (notes == null) {
			throw new NullValueException("There is no any details stored in note yet");
		}

		return notes.stream().filter(noteStream->!noteStream.isTrashed()&&noteStream.isPin()).map(filterNote->modelMapper.map(filterNote,ViewNoteDTO.class)).collect(Collectors.toList());
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

		//Optional<Note> checkNote = noteRepository.findByNoteId(noteId);
		Optional<Note> checkNote = noteElasticRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		Note note=checkNote.get();
		
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException("Please enter valid token to match your account");
		}
		
			note.setTrashed(choice);
			
		noteRepository.save(note);
		noteElasticRepository.save(note);
	}

}
