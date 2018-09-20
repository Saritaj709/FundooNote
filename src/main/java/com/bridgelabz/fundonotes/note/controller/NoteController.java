package com.bridgelabz.fundonotes.note.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundonotes.note.exception.DateException;
import com.bridgelabz.fundonotes.note.exception.LabelAdditionException;
import com.bridgelabz.fundonotes.note.exception.LabelNotFoundException;
import com.bridgelabz.fundonotes.note.exception.MalFormedException;
import com.bridgelabz.fundonotes.note.exception.NoteArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NotePinnedException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnPinnedException;
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.RestHighLevelClientException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UrlAdditionException;
import com.bridgelabz.fundonotes.note.exception.UnAuthorizedException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.ResponseDTONote;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.UrlMetaData;
import com.bridgelabz.fundonotes.note.model.ViewNoteDTO;
import com.bridgelabz.fundonotes.note.services.ContentScrapService;
import com.bridgelabz.fundonotes.note.services.NoteService;
import com.bridgelabz.fundonotes.user.model.Response;

/**
 * @author Sarita
 *
 */

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	@Autowired
	private NoteService noteService;
	
	/*@Autowired
	private NoteDao noteService;
*/
	@Autowired
	private ContentScrapService scrapService;

	// -------------Create A New Note----------------------
	/**
	 * 
	 * @param req
	 * @param createDto
	 * @return Note
	 * @throws NoteCreationException
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws DateException
	 * @throws LabelNotFoundException
	 * @throws NullValueException
	 * @throws MalFormedException
	 * @throws IOException 
	 */
	@PostMapping(value = "/create")
	public ResponseEntity<Response> createNote(HttpServletRequest req, @RequestBody CreateDTO createDto)
			throws NoteCreationException, NoteNotFoundException, UnAuthorizedException, DateException,
			LabelNotFoundException, NullValueException, MalFormedException, IOException {

		String userId = (String) req.getAttribute("userId");
          noteService.createNote(userId, createDto);
          Response response=new Response("note created",12);
          return new ResponseEntity<>(response,HttpStatus.CREATED);
		//ViewNoteDTO viewNoteDto = noteService.createNote(userId, createDto);
		
		//return new ResponseEntity<>(viewNoteDto, HttpStatus.CREATED);
	}

	/**
	 * @param req
	 * @param noteId
	 * @param url
	 * @return UrlMetaData
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws MalFormedException
	 */
	@PostMapping(value = "/scrap")
	public ResponseEntity<List<UrlMetaData>> createContent(HttpServletRequest req,
			@RequestParam(value = "link-url") String url)
			throws IOException, NoteNotFoundException, UnAuthorizedException, MalFormedException {
		 req.getAttribute("userId");

		List<UrlMetaData> urlMetaData = scrapService.addContent(url);
		return new ResponseEntity<>(urlMetaData, HttpStatus.CREATED);
	}

	// --------------------Add Content To Particular Note------------
	@PostMapping(value = "/addscrap-to-note")
	public ResponseEntity<ResponseDTONote> addContentToNote(HttpServletRequest req,
			@RequestParam(value = "noteId") String noteId, @RequestParam(value = "link-url") String url)
			throws IOException, NoteNotFoundException, UnAuthorizedException, MalFormedException, UrlAdditionException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");
		noteService.addContentToNote(userId, noteId, url);

		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Content succesfully added to note");
		response.setStatus(31);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ---------------Add Label To Notes-----------------------

	/**
	 * 
	 * @param req
	 * @param labelName
	 * @param noteId
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 * @throws LabelAdditionException
	 * @throws RestHighLevelClientException 
	 * @throws IOException 
	 */
	@PostMapping(value = "/add-label/{noteId}")
	public ResponseEntity<ResponseDTONote> addLabelToNotes(HttpServletRequest req,
			@RequestParam(value = "labelName") String labelName, @PathVariable(value = "noteId") String noteId)
			throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException, LabelAdditionException, RestHighLevelClientException, IOException {

		String userId = (String) req.getAttribute("userId");

		noteService.addLabel(userId, labelName, noteId);

		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Label is successfully added");
		response.setStatus(15);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// -------------Delete A Particular Label From A Particular Note-----

	/**
	 * 
	 * @param req
	 * @param noteId
	 * @param labelId
	 * @return response
	 * @throws Exception
	 */
	@DeleteMapping(value = "/deletelabel-from-particular-note/{labelId}")
	public ResponseEntity<ResponseDTONote> deleteLabelFromParticularNote(HttpServletRequest req,
			@RequestParam(value = "NoteId") String noteId, @PathVariable(value = "labelId") String labelId)
			throws Exception {

		String userId = (String) req.getAttribute("userId");

		noteService.removeLabelFromNote(userId, noteId, labelId);

		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Label from Note is successfully deleted");
		response.setStatus(20);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ------------Update An Existing Note-------------------

	/**
	 * 
	 * @param req
	 * @param updateDto
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 * @throws IOException 
	 * @throws RestHighLevelClientException 
	 */
	@PutMapping(value = "/update")
	public ResponseEntity<ResponseDTONote> updateNote(HttpServletRequest req, @RequestBody UpdateDTO updateDto)
			throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException, RestHighLevelClientException, IOException {

		String userId = (String) req.getAttribute("userId");

		noteService.updateNote(userId, updateDto);
          //noteDao.updateNote(userId, updateDto);
		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Note successfully updated");
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// --------------------View trashed Notes--------------------

	/**
	 * 
	 * @return List of Trashed Notes
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws NullValueException
	 * @throws IOException 
	 */
	@GetMapping("/view-trashed")
	public ResponseEntity<List<ViewNoteDTO>> viewTrashedNotes(HttpServletRequest req)
			throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException, NullValueException, IOException {

		String userId = (String) req.getAttribute("userId");
		noteService.viewTrashed(userId);

		return new ResponseEntity<>(noteService.viewTrashed(userId), HttpStatus.OK);
	}

	// ------------------Move To Trash Or Restore A Note-----------------------

	/**
	 * 
	 * @param req
	 * @param noteId
	 * @param choice
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	@PostMapping(value = "/delete-restore/{noteId}")
	public ResponseEntity<ResponseDTONote> deleteOrRestoreNote(HttpServletRequest req,
			@PathVariable(value = "noteId") String noteId,
			@RequestParam(value = "choice,true-delete/false-restore") boolean choice)
			throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException {

		String userId = (String) req.getAttribute("userId");

		noteService.deleteOrRestoreNote(userId, noteId, choice);

		ResponseDTONote response = new ResponseDTONote();

		if (choice) {
			response.setMessage("Note is successfully trashed");
			response.setStatus(117);
		}

		else {
			response.setMessage("Note is successfully restored");
			response.setStatus(119);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ----------Delete An Existing Note From Trash--------------

	/**
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 * @throws RestHighLevelClientException 
	 */
	@DeleteMapping(value = "/delete-note-forever/{noteId}")
	public ResponseEntity<ResponseDTONote> deleteNoteForever(@PathVariable(value = "noteId") String noteId,
			HttpServletRequest req)
			throws NoteNotFoundException, UnAuthorizedException, UntrashedException, NoteTrashedException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");

		noteService.deleteNoteForever(userId, noteId);

		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Note successfully deleted");
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------------To Archive Notes----------------------------

	/**
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NoteTrashedException
	 * @throws NoteArchievedException
	 * @throws NoteUnArchievedException
	 * @throws RestHighLevelClientException 
	 */
	@PutMapping(value = "/archive-unarchive/{noteId}")
	public ResponseEntity<ResponseDTONote> archieveOrUnArchieveNote(HttpServletRequest req,
			@PathVariable(value = "noteId") String noteId,
			@RequestParam(value = "choice,true-archieve,false-unarchieve") boolean choice) throws NoteNotFoundException,
			UnAuthorizedException, NoteTrashedException, NoteArchievedException, NoteUnArchievedException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");

		noteService.archieveOrUnArchieveNote(userId, noteId, choice);

		ResponseDTONote response = new ResponseDTONote();

		if (choice) {
			response.setMessage("Note is successfully archieved");
			response.setStatus(11);
		}

		else {
			response.setMessage("Note is successfully unarchieved");
			response.setStatus(24);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// --------------View Archived Notes--------------------------------

	/**
	 * 
	 * @return List of Archived Notes
	 * @throws NullValueException
	 * @throws IOException 
	 */
	@GetMapping(value = "/view-archived-notes")
	public ResponseEntity<List<ViewNoteDTO>> viewArchievedNotes(HttpServletRequest req) throws NullValueException, IOException {

		String userId = (String) req.getAttribute("userId");
		noteService.viewArchieved(userId);

		return new ResponseEntity<>(noteService.viewArchieved(userId), HttpStatus.OK);
	}

	// -------------------To Pin Or Unpin Notes---------------------------------

	/**
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UnAuthorizedException
	 * @throws NotePinnedException
	 * @throws NoteTrashedException
	 * @throws NoteUnPinnedException
	 * @throws RestHighLevelClientException 
	 */
	@PostMapping(value = "/pin-unpin/{noteId}")
	public ResponseEntity<ResponseDTONote> pinorunpinNote(HttpServletRequest req, @PathVariable String noteId,
			@RequestParam(value = "choice,true-pin/false-unpin") boolean choice) throws NoteNotFoundException,
			UnAuthorizedException, NotePinnedException, NoteTrashedException, NoteUnPinnedException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");

		noteService.pinOrUnpinNote(userId, noteId, choice);

		ResponseDTONote response = new ResponseDTONote();

		if (choice) {
			response.setMessage("Note is successfully pinned");
			response.setStatus(12);
		}

		else {
			response.setMessage("Note is successfully unpinned");
			response.setStatus(22);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------------View Pinned Notes----------------------------

	/**
	 * 
	 * @return List of Pinned Notes
	 * @throws NullValueException
	 * @throws IOException 
	 */
	@GetMapping(value = "/view-pinned-notes")
	public ResponseEntity<List<ViewNoteDTO>> viewPinnedNotes(HttpServletRequest req) throws NullValueException, IOException {

		String userId = (String) req.getAttribute("userId");
		noteService.viewPinned(userId);

		return new ResponseEntity<>(noteService.viewPinned(userId), HttpStatus.OK);
	}

	// ----------------Read Entire Details Of All The Notes------------

	/**
	 * 
	 * @return List of Notes
	 * @throws NullValueException
	 * @throws NoteNotFoundException
	 * @throws NoteCreationException
	 * @throws UnAuthorizedException
	 */
	@GetMapping("/read-all-notes")
	public ResponseEntity<List<ViewNoteDTO>> readAllNotes()
			throws NullValueException, NoteNotFoundException, NoteCreationException, UnAuthorizedException {

		noteService.readAllNotes();

		return new ResponseEntity<>(noteService.readAllNotes(), HttpStatus.OK);
	}

	// -----------------Read Notes Of A Particular User------------------

	/**
	 * 
	 * @param req
	 * @return List of Notes of A Particular User
	 * @throws NullValueException
	 * @throws NoteNotFoundException
	 * @throws NoteCreationException
	 * @throws UnAuthorizedException
	 * @throws IOException 
	 */
	@GetMapping("/read-user-notes")
	public ResponseEntity<List<ViewNoteDTO>> readUserNotes(HttpServletRequest req)
			throws NullValueException, NoteNotFoundException, NoteCreationException, UnAuthorizedException, IOException {

		String userId = (String) req.getAttribute("userId");
		noteService.readUserNotes(userId);

		return new ResponseEntity<>(noteService.readUserNotes(userId), HttpStatus.OK);
	}

	// ----------Read A Particular Note-------------------------------

	/**
	 * 
	 * @param req
	 * @param noteId
	 * @return A Particular Note By Its Id
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws RestHighLevelClientException 
	 */
	@PostMapping("/getnote/{noteId}")
	public ResponseEntity<ViewNoteDTO> readParticularNote(HttpServletRequest req, @PathVariable("noteId") String noteId)
			throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");

		noteService.findNoteById(userId, noteId);

		return new ResponseEntity<>(noteService.findNoteById(userId, noteId), HttpStatus.OK);
	}

	// -----------Add A Color To A Note-----------------------------------

	/**
	 * 
	 * @param req
	 * @param color
	 * @param noteId
	 * @return response
	 * @throws NoteCreationException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws DateException
	 * @throws RestHighLevelClientException 
	 */
	@RequestMapping(value = "/add-color/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseDTONote> addNoteColor(HttpServletRequest req, @RequestParam(value = "color") String color,
			@PathVariable String noteId) throws NoteCreationException, UnAuthorizedException, NoteNotFoundException,
			NoteTrashedException, DateException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");

		noteService.addColor(userId, color, noteId);

		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Color is successfully set!!");
		response.setStatus(21);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------Add A Reminder To A Particular Note---------------------

	/**
	 * 
	 * @param req
	 * @param date
	 * @param noteId
	 * @return response
	 * @throws NoteCreationException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws DateException
	 * @throws RestHighLevelClientException 
	 */
	@RequestMapping(value = "/add-reminder/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseDTONote> addNoteReminder(HttpServletRequest req,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @PathVariable String noteId)
			throws NoteCreationException, UnAuthorizedException, NoteNotFoundException, NoteTrashedException,
			DateException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");

		noteService.addReminder(userId, date, noteId);

		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Reminder is successfully set!!");
		response.setStatus(7);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// -------Delete Reminder From A Note---------------------------------

	/**
	 * 
	 * @param req
	 * @param noteId
	 * @return response
	 * @throws NullValueException
	 * @throws UnAuthorizedException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws RestHighLevelClientException 
	 */
	@RequestMapping(value = "/delete-reminder/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<ResponseDTONote> deleteNoteReminder(HttpServletRequest req, @PathVariable String noteId)
			throws NullValueException, UnAuthorizedException, NoteNotFoundException, NoteTrashedException, RestHighLevelClientException {

		String userId = (String) req.getAttribute("userId");

		noteService.deleteReminder(userId, noteId);

		ResponseDTONote response = new ResponseDTONote();

		response.setMessage("Reminder is successfully removed!!");
		response.setStatus(9);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------------Sort Note By Date or Title-------------------------
	/**
	 * @param req
	 * @param order
	 * @return noteDTO sorted by date
	 * @throws NullValueException
	 * @throws IOException 
	 */
	@GetMapping(value = "/sort-notes-by-date-or-title")
	public ResponseEntity<List<ViewNoteDTO>> viewNotesBySortedDateOrTitle(HttpServletRequest req,
			@RequestParam(value = "order,asc/desc", required = false) String order,
			@RequestParam(value = "sortBy,date/title", required = false) String choice) throws NullValueException, IOException {

		String userId = (String) req.getAttribute("userId");
		noteService.viewNotesBySortedDateOrTitle(userId, order, choice);

		return new ResponseEntity<>(noteService.viewNotesBySortedDateOrTitle(userId, order, choice), HttpStatus.OK);
	}
	
	//----------------------------Add Image To Note----------------------------//
	
	@PostMapping(value = "/add-image-to-note")
	public ResponseEntity<?> addImageToNote(HttpServletRequest req, @RequestParam(value = "noteId") String noteId,
			@RequestParam(value = "file") MultipartFile image) throws NoteNotFoundException, NoteTrashedException, UnAuthorizedException, IOException, RestHighLevelClientException {
		String userId = (String) req.getAttribute("userId");
		noteService.addImageToNote(userId, noteId, image);
		return new ResponseEntity<>("saved the upload",HttpStatus.OK);
	}
	
	//------------------------Remove Image From A Note---------------------------//
	
	@DeleteMapping(value = "/remove-image-from-note")
	public ResponseEntity<String> removeImageFromNote(HttpServletRequest req, @RequestParam(value = "noteId") String noteId,
			@RequestParam(value = "image") String image) throws NoteNotFoundException, NoteTrashedException, UnAuthorizedException, NullValueException, RestHighLevelClientException {
		String userId = (String) req.getAttribute("userId");
		String pic= noteService.removeImageFromNote(userId, noteId, image);
		return new ResponseEntity<>(pic,HttpStatus.OK);
	}
}
