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
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;
import com.bridgelabz.fundonotes.note.model.Label;
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

	@Override
	/**
	 * @param userId
	 * @author Sarita
	 * @Return Note
	 */
	public Note createNote(String userId, CreateDTO createDto) throws NoteNotFoundException, NoteCreationException,
			UserNotFoundException, DateException, LabelNotFoundException, NullEntryException {

		NoteUtility.validateNoteCreation(createDto);

		Note noteDto = modelMapper.map(createDto, Note.class);

		if (createDto.getColor().equals(null) || createDto.getColor().length() == 0
				|| createDto.getColor().trim().length() == 0) {
			noteDto.setColor(Color);
		}
		if (createDto.getSetReminder().before(new Date())) {
			throw new DateException("reminder cannot be before current date");

		}

		noteDto.setUserId(userId);
		noteDto.setCreatedAt(new Date());
		noteDto.setLastModifiedAt(new Date());

		for (int i = 0; i < createDto.getLabel().size(); i++) {

			List<Label> labelList = labelRepository.findByLabelName(createDto.getLabel().get(i).getLabelName());

			if (labelList.isEmpty()) {

				CreateLabelDTO labelDto = new CreateLabelDTO();

				labelDto.setLabelName(createDto.getLabel().get(i).getLabelName());
				labelDto.setUserId(userId);
				createLabel(userId, labelDto);
				List<Label> labelList1 = labelRepository.findByLabelName(createDto.getLabel().get(i).getLabelName());
				noteDto.setLabel(labelList1);

			}
		}

		noteRepository.save(noteDto);

		return noteDto;

	}

	@Override
	public Label createLabel(String userId, CreateLabelDTO createLabelDto)
			throws UserNotFoundException, NullEntryException, NoteNotFoundException {
		// TODO Auto-generated method stub

		NoteUtility.validateLabelCreation(createLabelDto);

		if (!userId.equals(createLabelDto.getUserId())) {
			throw new UserNotFoundException("The user with given id does not exist");
		}

		List<Label> labelList = labelRepository.findByLabelName(createLabelDto.getLabelName());

		if (!labelList.isEmpty()) {
			throw new LabelCreationException("label with this name already exists");
		}

		// Label labelDto = modelMapper.map(createLabelDto, Label.class);
		Label labelDto = new Label();
		labelDto.setLabelName(createLabelDto.getLabelName());
		labelDto.setUserId(createLabelDto.getUserId());

		labelRepository.save(labelDto);

		return labelDto;
	}

	@Override
	public void addLabel(String userId, String labelName, String noteId)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException, LabelAdditionException {
		// TODO Auto-generated method stub

		Optional<Note> checkNote = noteRepository.findById(noteId);
		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The note with given id does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		List<Label> labelList = labelRepository.findByLabelName(labelName);

		if (labelList.isEmpty()) {
			throw new NoSuchLabelException("The label with the given name does not exist");
		}

		if (checkNote.get().isTrashed()) {
			throw new NoteTrashedException("this note no longer exists");
		}

		List<Label> tempList = new LinkedList<>();

		tempList = checkNote.get().getLabel();

		if (tempList != null) {

			for (int i = 0; i < tempList.size(); i++) {

				if (tempList.get(i).getLabelName().equalsIgnoreCase(labelName)) {
					throw new LabelAdditionException("the label with this name already exists");
				}
			}
			tempList.addAll(labelList);

			checkNote.get().setLabel(tempList);
		}

		else {
			checkNote.get().setLabel(labelList);
		}
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
	public List<ViewNoteDTO> viewLabel(String userId, String labelId)
			throws LabelNotFoundException, UserNotFoundException, NoteNotFoundException {
		// TODO Auto-generated method stub
		Optional<Label> label = labelRepository.findByLabelId(labelId);
		if (!label.isPresent()) {
			throw new LabelNotFoundException("The label with the given id does not exist");
		}

		if (!label.get().getUserId().equals(userId)) {
			throw new UserNotFoundException("The user with the given id does not exist");
		}

		ArrayList<Note> noteList = (ArrayList<Note>) noteRepository.findAllByUserId(userId);

		if (noteList == null) {
			throw new NoteNotFoundException("no such note available");
		}

		ArrayList<ViewNoteDTO> viewList = new ArrayList<>();
		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);

			for (int j = 0; j < note.getLabel().size(); j++) {
				if (note.getLabel().get(j).getLabelId().equals(labelId)) {

					ViewNoteDTO viewDto = modelMapper.map(noteList.get(i), ViewNoteDTO.class);

					viewList.add(viewDto);

				}
			}
		}

		return viewList;
	}

	@Override
	public void editOrRemoveLabel(String userId, Label labelDto, String choice) throws Exception {
		// TODO Auto-generated method stub

		if (!userId.equals(labelDto.getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		Optional<Label> label = labelRepository.findByLabelId(labelDto.getLabelId());

		if (!label.isPresent()) {
			throw new NoSuchLabelException("The label with the given id does not exist");
		}

		List<Note> checkNote = noteRepository.findAllByUserId(labelDto.getUserId());
		if (checkNote.isEmpty()) {
			throw new NoteNotFoundException("Note Not Found Exception");
		}

		List<Note> noteList = new LinkedList<>();

		noteList = checkNote;
		List<Label> labelList = new LinkedList<>();

		if (choice.equalsIgnoreCase("edit")) {

			labelDto.setLabelName(labelDto.getLabelName());
			labelDto.setLabelId(label.get().getLabelId());
			labelDto.setUserId(label.get().getUserId());
			labelRepository.save(labelDto);
		}

		if (choice.equalsIgnoreCase("delete")) {

			for (int i = 0; i < noteList.size(); i++) {

				labelList = noteList.get(i).getLabel();

				for (int j = 0; j < labelList.size(); j++) {

					if (labelList.get(j).getLabelName().equalsIgnoreCase(labelDto.getLabelName())) {

						labelList.remove(label.get());

					}
				}
				noteList.get(i).setLabel(labelList);
				noteRepository.save(noteList.get(i));
				System.out.println("removed from list : " + label.get());
			}

			labelRepository.deleteByLabelId(labelDto.getLabelId());
		}
	}

	@Override
	public void removeLabel(String userId, String labelId)
			throws NoteNotFoundException, LabelNotFoundException, UserNotFoundException {

		Optional<Label> label = labelRepository.findByLabelId(labelId);
		if (!label.isPresent()) {
			throw new LabelNotFoundException("label is not present in list");
		}

		if (!label.get().getUserId().equals(userId)) {
			throw new UserNotFoundException("the user with given id is not found");
		}
		ArrayList<Note> noteList = (ArrayList<Note>) noteRepository.findAllByUserId(userId);
		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			for (int j = 0; j < note.getLabel().size(); j++) {
				if (note.getLabel().get(j).getLabelId().equals(labelId)) {
					note.getLabel().remove(i);
					noteRepository.save(note);
				}
			}
		}

		labelRepository.deleteByLabelId(labelId);
	}

	@Override
	public void removeLabelFromNote(String userId, String noteId, String labelId)
			throws LabelNotFoundException, NoteNotFoundException, UserNotFoundException {
		// TODO Auto-generated method stub
		Optional<Label> label = labelRepository.findByLabelId(labelId);
		if (!label.isPresent()) {
			throw new LabelNotFoundException("the label with given id does not exist");
		}

		Optional<Note> optionalNote = noteRepository.findByNoteId(noteId);
		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException("the note with given id does not exist");
		}

		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UserNotFoundException("this particular note is not authorized for given user");
		}

		Note note = optionalNote.get();
		for (int i = 0; i < note.getLabel().size(); i++) {
			if (note.getLabel().get(i).getLabelId().equals(labelId)) {
				note.getLabel().remove(i);
				noteRepository.save(note);
			}
		}
	}

	@Override
	public void editLabel(String userId, String labelId, String labelName)
			throws LabelNotFoundException, UserNotFoundException {
		// TODO Auto-generated method stub
		Optional<Label> label = labelRepository.findByLabelId(labelId);
		if (!label.isPresent()) {
			throw new LabelNotFoundException("label is not present in list");
		}

		if (!label.get().getUserId().equals(userId)) {
			throw new UserNotFoundException("the user with given id is not found");
		}
		ArrayList<Note> noteList = (ArrayList<Note>) noteRepository.findAllByUserId(userId);
		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			for (int j = 0; j < note.getLabel().size(); j++) {
				if (note.getLabel().get(j).getLabelId().equals(labelId)) {
					note.getLabel().get(i).setLabelName(labelName);
					noteRepository.save(note);
				}
			}
		}
		label.get().setLabelName(labelName);
		labelRepository.save(label.get());
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
	public List<ViewNoteDTO> viewTrashed() throws NullEntryException {
		// TODO Auto-generated method stub

		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
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

	@Override
	public List<ViewNoteDTO> readAllNotes() throws NullEntryException {

		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
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

	@Override
	public List<ViewNoteDTO> readUserNotes(String userId) throws NullEntryException {
		// TODO Auto-generated method stub
		List<Note> noteList = noteRepository.findAllByUserId(userId);
		if (noteList.isEmpty()) {
			throw new NullEntryException("the note for given user is empty");
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

	@Override
	public ViewNoteDTO findNoteById(String userId, String noteId)
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

		ViewNoteDTO viewDto = modelMapper.map(checkNote.get(), ViewNoteDTO.class);

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
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, DateException {

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

		if (date.before(new Date())) {
			throw new DateException("reminder cannot be before current date");

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
	public List<ViewNoteDTO> viewArchieved() throws NullEntryException {
		// TODO Auto-generated method stub
		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
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
	public List<ViewNoteDTO> viewPinned() throws NullEntryException {
		// TODO Auto-generated method stub
		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullEntryException("There is no any details stored in note yet");
		}

		List<ViewNoteDTO> pinnedList = new LinkedList<>();

		for (int index = 0; index < noteList.size(); index++) {

			if (!noteList.get(index).isTrashed()) {

				if (noteList.get(index).isPin()) {

					ViewNoteDTO viewDto = modelMapper.map(noteList.get(index), ViewNoteDTO.class);
					pinnedList.add(viewDto);
				}
			}
		}
		return pinnedList;
	}

	@Override
	public void deleteOrRestoreNote(String userId, String noteId, String choice)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {
		// TODO Auto-generated method stub

		Optional<Note> checkNote = noteRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException("The given note does not exist");
		}

		if (!userId.equals(checkNote.get().getUserId())) {
			throw new UserNotFoundException("Please enter valid token to match your account");
		}

		if (choice.equalsIgnoreCase("restore")) {

			if (!checkNote.get().isTrashed()) {
				throw new UntrashedException("Note is already restored,it is not trashed yet");
			}

			checkNote.get().setTrashed(false);
			noteRepository.save(checkNote.get());
		}

		if (choice.equalsIgnoreCase("delete")) {

			if (checkNote.get().isTrashed()) {
				throw new NoteTrashedException("the note with given details is already trashed");
			}

			checkNote.get().setTrashed(true);
			noteRepository.save(checkNote.get());

		}
	}

}
