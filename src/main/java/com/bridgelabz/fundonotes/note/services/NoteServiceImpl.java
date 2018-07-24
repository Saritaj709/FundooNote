package com.bridgelabz.fundonotes.note.services;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.bridgelabz.fundonotes.note.model.LabelDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;
import com.bridgelabz.fundonotes.note.model.ViewLabelDTO;
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

	@Override
	public void createNote(String token, CreateDTO createDto)
			throws NoteNotFoundException, NoteCreationException, UserNotFoundException {

		NoteUtility.validateNoteCreation(createDto);

		String id = jwtToken.parseJwtToken(token);
		String userId = createDto.getUserId();

		if (!id.equals(userId)) {
			throw new UserNotFoundException("The user with given id does not exist");
		}

		NoteDTO noteDto = modelMapper.map(createDto, NoteDTO.class);

		if (createDto.getColor().equals(null) || createDto.getColor().length() == 0
				|| createDto.getColor().trim().length() == 0) {
			noteDto.setColor("white");
		}
		noteDto.setUserId(id);
		noteDto.setCreatedAt(new Date());
		noteDto.setSetReminder(null);
		noteDto.setLastModifiedAt(new Date());

		noteRepository.save(noteDto);

	}

	@Override
	public void createLabel(String token, CreateLabelDTO createLabelDto)
			throws UserNotFoundException, NullEntryException, NoteNotFoundException {
		// TODO Auto-generated method stub

		NoteUtility.validateNoteCreation(createLabelDto);

		Optional<NoteDTO> checkNote = noteRepository.findByNoteId(createLabelDto.getNoteId());
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		String id = jwtToken.parseJwtToken(token);

		if (!id.equals(createLabelDto.getUserId())) {
			throw new UserNotFoundException("The user with given id does not exist");
		}

		//LabelDTO labelDto = modelMapper.map(createLabelDto,LabelDTO.class);
		LabelDTO labelDto=new LabelDTO();
		List<LabelDTO> labelList=labelRepository.findByLabelName(createLabelDto.getLabelName());
		
		if(!labelList.isEmpty()) {
			throw new LabelCreationException("label with this name already exists");
		}
		
		labelDto.setLabelName(createLabelDto.getLabelName());
		labelDto.setNoteId(createLabelDto.getNoteId());
		labelDto.setUserId(id);
		
		labelRepository.save(labelDto);
	}

	@Override
	public void addLabel(String token, String labelName, String noteId) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {
		// TODO Auto-generated method stub
		String id = jwtToken.parseJwtToken(token);

		Optional<NoteDTO> checkNote = noteRepository.findById(noteId);
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		if (!id.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		List<LabelDTO> labelList = labelRepository.findByLabelName(labelName);
		
		if(labelList.isEmpty()) {
			throw new NoSuchLabelException("The label with the given id does not exist");
		}
		
		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}
	
		checkNote.get().setLabel(labelList);
		noteRepository.save(checkNote.get());
	
	}
	

	@Override
	public List<ViewLabelDTO> viewLabels() throws NullEntryException {
		// TODO Auto-generated method stub
		List<LabelDTO> labelList = labelRepository.findAll();

		if (labelList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<ViewLabelDTO> viewList = new LinkedList<>();

		for (int index = 0; index < labelList.size(); index++) {

				ViewLabelDTO viewLabelDto = new ViewLabelDTO();
				
				viewLabelDto.setLabelId(labelList.get(index).getLabelId());
				viewLabelDto.setLabelName(labelList.get(index).getLabelName());
				viewList.add(viewLabelDto);		
			 }
		return viewList;		
	}
	
	@Override
	public void editOrRemoveLabel(String token,LabelDTO labelDto,String choice) throws Exception {
		// TODO Auto-generated method stub
		String id = jwtToken.parseJwtToken(token);

		/*Optional<NoteDTO> checkNote = noteRepository.findById(labelDto.getNoteId());
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}*/

		if (!id.equals(labelDto.getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		//List<LabelDTO> labelList = labelRepository.findByLabelName(labelDto.getLabelName());
		Optional<LabelDTO> label=labelRepository.findByLabelId(labelDto.getLabelId());
		
		if(!label.isPresent()) {
			throw new NoSuchLabelException("The label with the given id does not exist");
		}
		
		if(choice.equalsIgnoreCase("edit")) {
	
			labelDto.setLabelName(labelDto.getLabelName());
			labelDto.setLabelId(label.get().getLabelId());
			labelDto.setNoteId(label.get().getNoteId());
			labelDto.setUserId(label.get().getUserId());
			labelRepository.save(labelDto);
		}
		
		if(choice.equalsIgnoreCase("delete")) {
			labelRepository.deleteByLabelId(labelDto.getLabelId());
		}
	}

	
	@Override
	public void updateNote(String token, UpdateDTO updateDto)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {

		String id = jwtToken.parseJwtToken(token);

		Optional<NoteDTO> checkNote = noteRepository.findById(updateDto.getNoteId());
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		if (!id.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}

		NoteDTO note = modelMapper.map(updateDto, NoteDTO.class);

		note.setCreatedAt(checkNote.get().getCreatedAt());
		note.setLastModifiedAt(new Date());
		note.setSetReminder(null);
		note.setColor(checkNote.get().getColor());
		note.setUserId(checkNote.get().getUserId());

		noteRepository.save(note);
	}

	/*@Override
	public boolean trashNote(String userId, String noteId)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		Optional<NoteDTO> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		// String id=jwtToken.parseJwtToken(token);

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("the note with given details is already trashed");
		}

		checkNote.get().setTrashed(true);
		noteRepository.save(checkNote.get());

		return true;
	}*/

	@Override
	public List<ViewDTO> viewTrashed() throws NullEntryException {
		// TODO Auto-generated method stub

		List<NoteDTO> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<ViewDTO> viewList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (noteList.get(index).isTrashed()) {

				ViewDTO viewDto = new ViewDTO();
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
			}
		}
		return viewList;

	}

	/*@Override
	public void restoreNote(String token, String noteId)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		Optional<NoteDTO> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		String id = jwtToken.parseJwtToken(token);

		if (!id.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (!checkNote.get().isTrashed()) {
			throw new UntrashedException("Note is already restored,it is not trashed yet");
		}

		checkNote.get().setTrashed(false);
		noteRepository.save(checkNote.get());

	}*/

	@Override
	public List<NoteDTO> readAllNotes() throws NullEntryException {

		List<NoteDTO> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}
		return noteList;
	}

	@Override
	public List<ViewDTO> readNotes() throws NullEntryException {

		List<NoteDTO> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<ViewDTO> viewList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				ViewDTO viewDto = new ViewDTO();
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
			}
		}
		return viewList;
	}

	@Override
	public ViewDTO findNoteById(String token, String noteId)
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		String id = jwtToken.parseJwtToken(token);

		Optional<NoteDTO> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		if (!id.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("the note with given details are already trashed");
		}

		ViewDTO viewDto = modelMapper.map(checkNote, ViewDTO.class);

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
	public void deleteNoteForever(String token, String noteId)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		Optional<NoteDTO> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		String id = jwtToken.parseJwtToken(token);

		if (!id.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (!checkNote.get().isTrashed()) {
			throw new UntrashedException("Note is not trashed yet");
		}

		noteRepository.deleteByNoteId(noteId);
	}

	@Override
	public boolean addReminder(String token, Date date, String noteId)
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		String id = jwtToken.parseJwtToken(token);

		Optional<NoteDTO> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		if (!id.equals(checkNote.get().getUserId())) {
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
	public void deleteReminder(String token, String noteId)
			throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		String id = jwtToken.parseJwtToken(token);

		Optional<NoteDTO> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		if (!id.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (checkNote.get().getSetReminder() == null) {
			throw new NullEntryException("There is no reminder for the note yet");
		}

		checkNote.get().setSetReminder(null);
		noteRepository.save(checkNote.get());
	}

	@Override
	public void archieveNote(String token, String noteId)
			throws NoteNotFoundException, UserNotFoundException, NoteArchievedException, NoteTrashedException {

		Optional<NoteDTO> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		String id = jwtToken.parseJwtToken(token);

		if (!id.equals(checkNote.get().getUserId())) {
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
	public List<ViewDTO> viewArchieved() throws NullEntryException {
		// TODO Auto-generated method stub
		List<NoteDTO> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<ViewDTO> viewList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				if (noteList.get(index).isArchieve()) {

					ViewDTO viewDto = new ViewDTO();
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
					return viewList;
				}
			}
		}

		return null;
	}

	@Override
	public void pinNote(String token, String noteId)
			throws NoteNotFoundException, UserNotFoundException, NotePinnedException, NoteTrashedException {

		Optional<NoteDTO> checkNote = noteRepository.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		String id = jwtToken.parseJwtToken(token);

		if (!id.equals(checkNote.get().getUserId())) {
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
	public List<ViewDTO> viewPinned() throws NullEntryException {
		// TODO Auto-generated method stub
		List<NoteDTO> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<ViewDTO> viewList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				if (noteList.get(index).isPin()) {

					ViewDTO viewDto = new ViewDTO();
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
					return viewList;
				}
			}
		}
		return null;
	}

	@Override
	public void deleteOrRestoreNote(String token, String noteId, String choice) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {
		// TODO Auto-generated method stub
		
		Optional<NoteDTO> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		String id = jwtToken.parseJwtToken(token);

		if (!id.equals(checkNote.get().getUserId())) {
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
