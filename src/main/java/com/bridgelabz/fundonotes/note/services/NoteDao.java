package com.bridgelabz.fundonotes.note.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.validator.routines.UrlValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundonotes.configurations.AwsConfigurations;
import com.bridgelabz.fundonotes.note.exception.DateException;
import com.bridgelabz.fundonotes.note.exception.LabelAdditionException;
import com.bridgelabz.fundonotes.note.exception.LabelNotFoundException;
import com.bridgelabz.fundonotes.note.exception.MalFormedException;
import com.bridgelabz.fundonotes.note.exception.NoSuchLabelException;
import com.bridgelabz.fundonotes.note.exception.NoteArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NotePinnedException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnPinnedException;
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.RestHighLevelClientException;
import com.bridgelabz.fundonotes.note.exception.UnAuthorizedException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UrlAdditionException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.Label;
import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.UrlMetaData;
import com.bridgelabz.fundonotes.note.model.ViewNoteDTO;
import com.bridgelabz.fundonotes.note.repository.HighElasticRepositoryForLabel;
import com.bridgelabz.fundonotes.note.repository.HighElasticRepositoryForNote;
import com.bridgelabz.fundonotes.note.repository.NoteRepository;
import com.bridgelabz.fundonotes.note.utility.NoteUtility;

@Service
public class NoteDao {
	
	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private HighElasticRepositoryForLabel labelElasticRepository;

	@Autowired
	private LabelService labelService;

	@Autowired
	private LabelDao labelDao;
	
	@Autowired
	private Environment environment;

	@Autowired
	private ContentScrapService scrap;

	@Autowired
	private ImageService awsS3Service;

	@Autowired
	private AwsConfigurations awsConfigurations;
	
	@Autowired
	private HighElasticRepositoryForNote highElasticRepositoryForNote;

	public ViewNoteDTO createNote(String userId, CreateDTO createDto)
			throws NoteNotFoundException, NoteCreationException, UnAuthorizedException, DateException,
			LabelNotFoundException, NullValueException, MalFormedException, IOException {

		NoteUtility.validateNoteCreation(createDto);

		Note note = modelMapper.map(createDto, Note.class);

		if (createDto.getColor().equals(null) || createDto.getColor().length() == 0
				|| createDto.getColor().trim().length() == 0) {
			note.setColor(environment.getProperty("Color"));
		}

		if (createDto.getReminder().before(new Date())) {
			throw new DateException(environment.getProperty("DateException"));

		}

		note.setUserId(userId);
		note.setCreatedAt(new Date());
		note.setLastModifiedAt(new Date());

		for (int i = 0; i < createDto.getLabels().size(); i++) {

			List<Label> labels = labelElasticRepository
					.findByLabelNameAndUserId(createDto.getLabels().get(i).getLabelName(), userId);

			if (labels.isEmpty()) {

				labelService.createLabel(userId, createDto.getLabels().get(i).getLabelName());

				List<Label> labels1 = labelElasticRepository
						.findByLabelNameAndUserId(createDto.getLabels().get(i).getLabelName(), userId);

				note.setLabels(labels1);

			}
		}

		String[] contents = createDto.getDescription().split(" ");
		if (contents.length > 0) {

			List<UrlMetaData> metaData = scrap.addSplitContent(createDto.getDescription());
			note.setMetaData(metaData);
		}
		List<String> descriptionList = new ArrayList<>();
		descriptionList.add(createDto.getDescription());
		note.setDescription(descriptionList);

		noteRepository.save(note);

		highElasticRepositoryForNote.save(note);
		
		ViewNoteDTO viewNoteDto = modelMapper.map(note, ViewNoteDTO.class);

		return viewNoteDto;
	}

	public void addContentToNote(String userId, String noteId, String url)
			throws MalFormedException, NoteNotFoundException, UnAuthorizedException, UrlAdditionException, RestHighLevelClientException {

		Optional<Note> optionalNote = highElasticRepositoryForNote.findByNoteId(noteId);
		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = optionalNote.get();
		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		List<String> descriptionList = new ArrayList<>();

		String[] contents = url.split(" ");

		descriptionList = note.getDescription();

		for (int i = 0; i < descriptionList.size(); i++) {

			for (int j = 0; j < contents.length; j++) {
				UrlValidator validator = new UrlValidator();
				if (validator.isValid(contents[j]) && descriptionList.contains(contents[j])) {
					throw new UrlAdditionException(environment.getProperty("UrlAdditionException"));
				}
			}
		}

		descriptionList.add(url);
		List<String> desList = descriptionList;
		note.setDescription(desList);

		List<UrlMetaData> listMetaData = new ArrayList<>();

		listMetaData = note.getMetaData();

		if (listMetaData != null) {

			if (contents.length > 0) {
				List<UrlMetaData> metaData = scrap.addSplitContent(url);

				listMetaData.addAll(metaData);
				note.setMetaData(listMetaData);

			}
			if (contents.length == 0) {
				List<UrlMetaData> data = scrap.addContent(url);
				listMetaData.addAll(data);
				note.setMetaData(listMetaData);
			}
		} else {

			if (contents.length > 0) {
				List<UrlMetaData> metaData = scrap.addSplitContent(url);

				note.setMetaData(metaData);
			}
			if (contents.length == 0) {
				List<UrlMetaData> data = scrap.addContent(url);
				note.setMetaData(data);
			}
		}
		noteRepository.save(note);
		highElasticRepositoryForNote.save(note);
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
	 * @throws RestHighLevelClientException 
	 * @throws IOException 
	 */

	public void addLabel(String userId, String labelId, String noteId)
			throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException, LabelAdditionException, RestHighLevelClientException, IOException {

		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		// List<Label> labels = labelRepository.findByLabelIdAndUserId(labelId,userId);

		List<Label> labels = labelElasticRepository.findByLabelNameAndUserId(labelId, userId);

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

		highElasticRepositoryForNote.save(note);

	}

	/**
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws LabelNotFoundException
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws RestHighLevelClientException 
	 */

	public void removeLabelFromNote(String userId, String noteId, String labelId)
			throws LabelNotFoundException, NoteNotFoundException, UnAuthorizedException, RestHighLevelClientException {

		// Optional<Label> optionalLabel = labelRepository.findByLabelId(labelId);

		Optional<Label> optionalLabel = labelElasticRepository.findById(labelId);
		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException(environment.getProperty("LabelNotFoundException"));
		}

		Label label = optionalLabel.get();

		// Optional<Note> optionalNote = noteRepository.findByNoteId(noteId);
		Optional<Note> optionalNote = highElasticRepositoryForNote.findByNoteId(noteId);
		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = optionalNote.get();

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		if (!label.getUserId().equals(userId)) {
			throw new UnAuthorizedException("this particular label is not authorized for given user");
		}

		for (int i = 0; i < note.getLabels().size(); i++) {
			if (note.getLabels().get(i).getLabelId().equals(labelId)) {
				note.getLabels().remove(i);
				noteRepository.save(note);
				highElasticRepositoryForNote.save(note);
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
	 * @throws RestHighLevelClientException
	 * @throws IOException
	 */

	public void updateNote(String userId, UpdateDTO updateDto) throws NoteNotFoundException, UnAuthorizedException,
			NoteTrashedException, RestHighLevelClientException, IOException {

		Optional<Note> checkNote = noteRepository.findById(updateDto.getNoteId());

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		Note note1 = modelMapper.map(updateDto, Note.class);

		note1.setCreatedAt(note.getCreatedAt());
		note1.setLastModifiedAt(new Date());
		note1.setReminder(note.getReminder());
		note1.setColor(note.getColor());
		note1.setUserId(note.getUserId());

		noteRepository.save(note1);
		highElasticRepositoryForNote.updateByNoteId(note1.getNoteId(),note1);
	}

	/**
	 * 
	 * @return List of Trashed Notes
	 * @throws NullValueException
	 * @throws IOException 
	 */

	public List<ViewNoteDTO> viewTrashed(String userId) throws NullValueException, IOException {

		List<Note> noteList = highElasticRepositoryForNote.findAllByUserIdAndTrashed(userId, true);

		if (noteList == null) {
			throw new NullValueException(environment.getProperty("NullValueException"));
		}

		return noteList.stream().map(filterNote -> modelMapper.map(filterNote, ViewNoteDTO.class))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @return List of Notes of A Particular user
	 * @throws NullValueException
	 */

	public List<ViewNoteDTO> readAllNotes() throws NullValueException {

		// List<Note> noteList = noteRepository.findAll();
		List<Note> noteList = noteRepository.findAll();

		if (noteList == null) {
			throw new NullValueException(environment.getProperty("NullValueException"));
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
	 * @throws IOException 
	 */

	public List<ViewNoteDTO> readUserNotes(String userId) throws NullValueException, IOException {

		List<Note> noteList = highElasticRepositoryForNote.findAllByUserIdAndTrashed(userId, false);
		// List<Note> noteList=noteElasticRepository.findAllByUserIdAndTrashed(userId,
		// false);
		if (noteList.isEmpty()) {
			throw new NullValueException(environment.getProperty("NullValueException"));
		}

		List<ViewNoteDTO> pin = noteList.stream().filter(noteStream -> noteStream.isPin())
				.map(filterNote -> modelMapper.map(filterNote, ViewNoteDTO.class)).collect(Collectors.toList());
		List<ViewNoteDTO> unPin = noteList.stream().filter(noteStream -> !noteStream.isPin())
				.map(filterNote -> modelMapper.map(filterNote, ViewNoteDTO.class)).collect(Collectors.toList());
		return Stream.concat(pin.stream(), unPin.stream()).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @return List of ViewNoteDTO
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws RestHighLevelClientException 
	 */

	public ViewNoteDTO findNoteById(String userId, String noteId)
			throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException, RestHighLevelClientException {

		// Optional<Note> checkNote = noteRepository.findById(noteId);

		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		ViewNoteDTO viewDto = modelMapper.map(note, ViewNoteDTO.class);

		return viewDto;
	}

	public void deleteNoteForever(String userId, String noteId)
			throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException, RestHighLevelClientException {

		// Optional<Note> checkNote = noteRepository.findByNoteId(noteId);

		Optional<Note> checkNote = highElasticRepositoryForNote.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (!note.isTrashed()) {
			throw new UntrashedException(environment.getProperty("UntrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		noteRepository.deleteByNoteId(noteId);
		highElasticRepositoryForNote.deleteById(noteId);
	}

	/**
	 * 
	 * @param userId
	 * @param color
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 * @throws RestHighLevelClientException 
	 */

	public void addColor(String userId, String color, String noteId)
			throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException, RestHighLevelClientException {

		// Optional<Note> checkNote = noteRepository.findById(noteId);

		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		note.setColor(color);
		noteRepository.save(note);
		highElasticRepositoryForNote.save(note);

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
	 * @throws RestHighLevelClientException 
	 */
	public void addReminder(String userId, Date date, String noteId)
			throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException, DateException, RestHighLevelClientException {

		if (date.before(new Date())) {
			throw new DateException(environment.getProperty("DateException"));
		}

		// Optional<Note> checkNote = noteRepository.findById(noteId);

		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty(" UnAuthorizedException"));
		}

		note.setReminder(date);

		noteRepository.save(note);
		highElasticRepositoryForNote.save(note);

	}

	/**
	 * 
	 * @param token
	 * @param noteId
	 * @throws NullValueException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws RestHighLevelClientException 
	 */
	public void deleteReminder(String userId, String noteId)
			throws NullValueException, UnAuthorizedException, NoteNotFoundException, NoteTrashedException, RestHighLevelClientException {

		// Optional<Note> checkNote = noteRepository.findById(noteId);
		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		note.setReminder(null);
		noteRepository.save(note);
		highElasticRepositoryForNote.save(note);
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
	 * @throws RestHighLevelClientException 
	 */

	public void archieveOrUnArchieveNote(String userId, String noteId, boolean choice) throws NoteNotFoundException,
			UnAuthorizedException, NoteArchievedException, NoteTrashedException, NoteUnArchievedException, RestHighLevelClientException {

		// Optional<Note> checkNote = noteRepository.findById(noteId);

		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		note.setArchieve(choice);

		noteRepository.save(note);
		highElasticRepositoryForNote.save(note);
	}

	/**
	 * 
	 * @return List of Archived Notes
	 * @throws NullValueException
	 * @throws IOException 
	 */

	public List<ViewNoteDTO> viewArchieved(String userId) throws NullValueException, IOException {

		// List<Note> noteList = noteRepository.findAll();
		List<Note> noteList = highElasticRepositoryForNote.findAllByUserIdAndTrashed(userId, false);

		if (noteList == null) {
			throw new NullValueException(environment.getProperty("NullValueException"));
		}

		return noteList.stream().filter(noteStream -> noteStream.isArchieve())
				.map(filterNote -> modelMapper.map(filterNote, ViewNoteDTO.class)).collect(Collectors.toList());
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
	 * @throws RestHighLevelClientException 
	 */

	public void pinOrUnpinNote(String userId, String noteId, boolean choice) throws NoteNotFoundException,
			UnAuthorizedException, NotePinnedException, NoteTrashedException, NoteUnPinnedException, RestHighLevelClientException {

		// Optional<Note> checkNote = noteRepository.findById(noteId);
		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		note.setPin(choice);

		noteRepository.save(note);
		highElasticRepositoryForNote.save(note);
	}

	/**
	 * 
	 * @return List of Pinned Notes
	 * @throws NullValueException
	 * @throws IOException 
	 */

	public List<ViewNoteDTO> viewPinned(String userId) throws NullValueException, IOException {

		// List<Note> notes = noteRepository.findAll();
		List<Note> notes = highElasticRepositoryForNote.findAllByUserIdAndTrashed(userId, false);
		// List<Note> notes = noteElasticRepository.findAllByUserId(userId);

		if (notes == null) {
			throw new NullValueException(environment.getProperty("NullValueException"));
		}

		return notes.stream().filter(noteStream -> noteStream.isPin())
				.map(filterNote -> modelMapper.map(filterNote, ViewNoteDTO.class)).collect(Collectors.toList());
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

	public void deleteOrRestoreNote(String userId, String noteId, boolean choice)
			throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException {

		Optional<Note> checkNote = noteRepository.findByNoteId(noteId);
		// Optional<Note> checkNote = noteElasticRepository.findByNoteId(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		note.setTrashed(choice);

		noteRepository.save(note);
		highElasticRepositoryForNote.save(note);
	}

	/**
	 * @param userId
	 * @param order
	 * @return noteDTO sorted by date
	 * @throws NullValueException
	 * @throws IOException 
	 */

	public List<ViewNoteDTO> viewNotesBySortedDateOrTitle(String userId, String order, String choice)
			throws NullValueException, IOException {

		List<Note> noteList = highElasticRepositoryForNote.findAllByUserIdAndTrashed(userId, false);
		if (noteList.isEmpty()) {
			throw new NullValueException(environment.getProperty("NullValueException"));
		}

		if (choice == null && order == null) {
			return noteList.stream().sorted(Comparator.comparing(Note::getCreatedAt).reversed())
					.map(SortedNote -> modelMapper.map(SortedNote, ViewNoteDTO.class)).collect(Collectors.toList());

		}

		if (choice.equals("date")) {

			if (order == null || order.matches(".*")) {
				return noteList.stream().sorted(Comparator.comparing(Note::getCreatedAt).reversed())
						.map(SortedNote -> modelMapper.map(SortedNote, ViewNoteDTO.class)).collect(Collectors.toList());
			}

			if (order.equalsIgnoreCase("asc")) {
				return noteList.stream().sorted(Comparator.comparing(Note::getCreatedAt))
						.map(SortedNote -> modelMapper.map(SortedNote, ViewNoteDTO.class)).collect(Collectors.toList());
			}

		}

		if (choice.equals("title")) {

			if (order == null || order.matches(".*")) {
				return noteList.stream().sorted(Comparator.comparing(Note::getTitle))
						.map(SortedNote -> modelMapper.map(SortedNote, ViewNoteDTO.class)).collect(Collectors.toList());

			}

			if (order.equals("desc")) {

				return noteList.stream().sorted(Comparator.comparing(Note::getTitle).reversed())
						.map(SortedNote -> modelMapper.map(SortedNote, ViewNoteDTO.class)).collect(Collectors.toList());
			}

		}

		return null;
	}

	public void addImageToNote(String userId, String noteId, MultipartFile image)
			throws NoteNotFoundException, NoteTrashedException, UnAuthorizedException, IOException, RestHighLevelClientException {
		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		String fileName = userId + environment.getProperty("suffix") + noteId;

	//	awsS3Service.uploadFile(fileName, image);

		//AmazonS3 client = awsConfigurations.getS3Client();

		List<String> tempList = note.getImage();

		//String url = ((AmazonS3Client) client).getResourceUrl(environment.getProperty("bucketName"), fileName);
		String url=environment.getProperty("imageLink") + userId + environment.getProperty("suffix")
		+ noteId + environment.getProperty("suffix");

		tempList.add(url);

		note.setImage(tempList);

		noteRepository.save(note);

		highElasticRepositoryForNote.save(note);
	}

	public String removeImageFromNote(String userId, String noteId, String url)
			throws NoteNotFoundException, NoteTrashedException, UnAuthorizedException, NullValueException, RestHighLevelClientException {

		Optional<Note> checkNote = highElasticRepositoryForNote.findById(noteId);

		if (!checkNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFoundException"));
		}

		Note note = checkNote.get();

		if (note.isTrashed()) {
			throw new NoteTrashedException(environment.getProperty("NoteTrashedException"));
		}

		if (!note.getUserId().equals(userId)) {
			throw new UnAuthorizedException(environment.getProperty("UnAuthorizedException"));
		}

		String[] images = url.split(environment.getProperty("imageLink") + userId + environment.getProperty("suffix")
				+ noteId + environment.getProperty("suffix"));
		System.out.println(images[1]);

		List<String> tempList = note.getImage();

		for (int i = 0; i < tempList.size(); i++) {

			if (!tempList.contains(
					environment.getProperty("imageLink") + userId + environment.getProperty("suffix") + images[1])) {
				throw new NullValueException(environment.getProperty("NullValueException"));

			}
		}

		tempList.remove(environment.getProperty("imageLink") + userId + environment.getProperty("suffix") + images[1]);
		note.setImage(tempList);

		//awsS3Service.deleteFile(url);

		noteRepository.save(note);

		highElasticRepositoryForNote.save(note);
		return url;
	}
}
