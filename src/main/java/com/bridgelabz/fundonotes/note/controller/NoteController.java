package com.bridgelabz.fundonotes.note.controller;

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

import com.bridgelabz.fundonotes.note.exception.DateException;
import com.bridgelabz.fundonotes.note.exception.LabelAdditionException;
import com.bridgelabz.fundonotes.note.exception.LabelNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NoteArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NotePinnedException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnArchievedException;
import com.bridgelabz.fundonotes.note.exception.NoteUnPinnedException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.CreateLabelDTO;
import com.bridgelabz.fundonotes.note.model.Label;
import com.bridgelabz.fundonotes.note.model.Note;
import com.bridgelabz.fundonotes.note.model.Response;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewNoteDTO;
import com.bridgelabz.fundonotes.note.services.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	@Autowired
	private NoteService noteService;

	// -------------Create A New Note----------------------
	/**
	 * 
	 * @param req
	 * @param createDto
	 * @return Note
	 * @throws NoteCreationException
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws DateException
	 * @throws LabelNotFoundException
	 * @throws NullEntryException
	 */
	@PostMapping(value = "/create-note")
	public ResponseEntity<Note> createNote(HttpServletRequest req, @RequestBody CreateDTO createDto)
			throws NoteCreationException, NoteNotFoundException, UserNotFoundException, DateException,
			LabelNotFoundException, NullEntryException {

		String userId = (String) req.getAttribute("token");

		Note note = noteService.createNote(userId, createDto);

		return new ResponseEntity<>(note, HttpStatus.CREATED);
	}

	// ------------------Add A Label--------------------------

	/**
	 * 
	 * @param req
	 * @param createLabelDto
	 * @return Label
	 * @throws NoteCreationException
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NullEntryException
	 */
	@PostMapping(value = "/create-label")
	public ResponseEntity<Label> createLabelInsideNote(HttpServletRequest req,
			@RequestBody CreateLabelDTO createLabelDto)
			throws NoteCreationException, NoteNotFoundException, UserNotFoundException, NullEntryException {

		String userId = (String) req.getAttribute("token");

		Label label = noteService.createLabel(userId, createLabelDto);

		return new ResponseEntity<>(label, HttpStatus.CREATED);
	}

	// ------------------View All Labels---------------------------

	/**
	 * 
	 * @return List of Labels
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws NullEntryException
	 */
	@GetMapping(value = "/view-all-labels")
	public ResponseEntity<List<Label>> viewAllLabels()
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, NullEntryException {

		noteService.viewLabels();

		return new ResponseEntity<>(noteService.viewLabels(), HttpStatus.OK);
	}

	// -----------------View Label-------------------------------

	/**
	 * 
	 * @param req
	 * @param labelId
	 * @return Label of A Particular User
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws NullEntryException
	 * @throws LabelNotFoundException
	 */
	@GetMapping(value = "/view-label/{labelId}")
	public ResponseEntity<List<ViewNoteDTO>> viewLabel(HttpServletRequest req,
			@PathVariable(value = "labelId") String labelId) throws UserNotFoundException, NoteNotFoundException,
			NoteTrashedException, NullEntryException, LabelNotFoundException {

		String userId = (String) req.getAttribute("token");
		noteService.viewLabel(userId, labelId);

		return new ResponseEntity<>(noteService.viewLabel(userId, labelId), HttpStatus.OK);
	}

	// ---------------Add Label To Notes-----------------------

	/**
	 * 
	 * @param req
	 * @param labelName
	 * @param noteId
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteTrashedException
	 * @throws LabelAdditionException
	 */
	@PostMapping(value = "/add-label/{noteId}")
	public ResponseEntity<Response> addLabelToNotes(HttpServletRequest req,
			@RequestParam(value = "labelName") String labelName, @PathVariable(value = "noteId") String noteId)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException, LabelAdditionException {

		String userId = (String) req.getAttribute("token");

		noteService.addLabel(userId, labelName, noteId);

		Response response = new Response();

		response.setMessage("Label is successfully added");
		response.setStatus(15);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// -------------Delete A Label------------------------------

	/**
	 * 
	 * @param req
	 * @param labelId
	 * @return response
	 * @throws Exception
	 */
	@DeleteMapping(value = "/delete-label/{labelId}")
	public ResponseEntity<Response> deleteLabel(HttpServletRequest req, @PathVariable(value = "labelId") String labelId)
			throws Exception {

		String userId = (String) req.getAttribute("token");

		noteService.removeLabel(userId, labelId);

		Response response = new Response();

		response.setMessage("Label is successfully deleted");
		response.setStatus(17);

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
	public ResponseEntity<Response> deleteLabelFromParticularNote(HttpServletRequest req,
			@RequestParam(value = "NoteId") String noteId, @PathVariable(value = "labelId") String labelId)
			throws Exception {

		String userId = (String) req.getAttribute("token");

		noteService.removeLabelFromNote(userId, noteId, labelId);

		Response response = new Response();

		response.setMessage("Label from Note is successfully deleted");
		response.setStatus(20);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// -------------Update A Label-----------------------------

	/**
	 * 
	 * @param req
	 * @param labelId
	 * @param labelName
	 * @return response
	 * @throws Exception
	 */
	@PutMapping(value = "/edit-label/{labelId}")
	public ResponseEntity<Response> editLabel(HttpServletRequest req, @PathVariable(value = "labelId") String labelId,
			@RequestParam(value = "editName") String labelName) throws Exception {

		String userId = (String) req.getAttribute("token");

		noteService.editLabel(userId, labelId, labelName);

		Response response = new Response();

		response.setMessage("Label is successfully updated");
		response.setStatus(19);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ------------Update An Existing Note-------------------

	/**
	 * 
	 * @param req
	 * @param updateDto
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteTrashedException
	 */
	@PutMapping(value = "/update")
	public ResponseEntity<Response> updateNote(HttpServletRequest req, @RequestBody UpdateDTO updateDto)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {

		String userId = (String) req.getAttribute("token");

		noteService.updateNote(userId, updateDto);

		Response response = new Response();

		response.setMessage("Note successfully updated");
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// --------------------View trashed Notes--------------------

	/**
	 * 
	 * @return List of Trashed Notes
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws NullEntryException
	 */
	@GetMapping("/view-trashed")
	public ResponseEntity<List<ViewNoteDTO>> viewTrashedNotes()
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, NullEntryException {

		noteService.viewTrashed();

		return new ResponseEntity<>(noteService.viewTrashed(), HttpStatus.OK);
	}

	// ------------------Move To Trash Or Restore A Note-----------------------

	/**
	 * 
	 * @param req
	 * @param noteId
	 * @param choice
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	@PostMapping(value = "/delete-restore/{noteId}")
	public ResponseEntity<Response> deleteOrRestoreNote(HttpServletRequest req, @PathVariable String noteId,
			@RequestParam(value = "choice,true-delete/false-restore") boolean choice)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		String userId = (String) req.getAttribute("token");

		noteService.deleteOrRestoreNote(userId, noteId, choice);

		Response response = new Response();
        
		if(choice) {
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
	 * @throws UserNotFoundException
	 * @throws UntrashedException
	 * @throws NoteTrashedException
	 */
	@DeleteMapping(value = "/delete-note-forever/{noteId}")
	public ResponseEntity<Response> deleteNoteForever(@PathVariable String noteId, HttpServletRequest req)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		String userId = (String) req.getAttribute("token");

		noteService.deleteNoteForever(userId, noteId);

		Response response = new Response();

		response.setMessage("Note successfully deleted");
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------------To Archieve Notes----------------------------

	/**
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NoteTrashedException
	 * @throws NoteArchievedException
	 * @throws NoteUnArchievedException
	 */
	@PostMapping(value = "/archieve-unarchieve/{noteId}")
	public ResponseEntity<Response> archieveOrUnArchieveNote(HttpServletRequest req, @PathVariable String noteId,
			@RequestParam(value = "choice,true-archieve,false-unarchieve") boolean choice) throws NoteNotFoundException,
			UserNotFoundException, NoteTrashedException, NoteArchievedException, NoteUnArchievedException {

		String userId = (String) req.getAttribute("token");

		noteService.archieveOrUnArchieveNote(userId, noteId, choice);

		Response response = new Response();

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

	// --------------View Archieved Notes--------------------------------

	/**
	 * 
	 * @return List of Archieved Notes
	 * @throws NullEntryException
	 */
	@GetMapping(value = "/view-archieved-notes")
	public ResponseEntity<List<ViewNoteDTO>> viewArchievedNotes() throws NullEntryException {

		noteService.viewArchieved();

		return new ResponseEntity<>(noteService.viewArchieved(), HttpStatus.OK);
	}

	// -------------------To Pin Or Unpin Notes---------------------------------

	/**
	 * 
	 * @param noteId
	 * @param req
	 * @return response
	 * @throws NoteNotFoundException
	 * @throws UserNotFoundException
	 * @throws NotePinnedException
	 * @throws NoteTrashedException
	 * @throws NoteUnPinnedException
	 */
	@PostMapping(value = "/pin-unpin/{noteId}")
	public ResponseEntity<Response> pinorunpinNote(HttpServletRequest req, @PathVariable String noteId,
			@RequestParam(value = "choice,true-pin/false-unpin") boolean choice) throws NoteNotFoundException,
			UserNotFoundException, NotePinnedException, NoteTrashedException, NoteUnPinnedException {

		String userId = (String) req.getAttribute("token");

		noteService.pinOrUnpinNote(userId, noteId, choice);

		Response response = new Response();

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
	 * @throws NullEntryException
	 */
	@GetMapping(value = "/view-pinned-notes")
	public ResponseEntity<List<ViewNoteDTO>> viewPinnedNotes() throws NullEntryException {

		noteService.viewPinned();

		return new ResponseEntity<>(noteService.viewPinned(), HttpStatus.OK);
	}

	// ----------------Read Entire Details Of All The Notes------------

	/**
	 * 
	 * @return List of Notes
	 * @throws NullEntryException
	 * @throws NoteNotFoundException
	 * @throws NoteCreationException
	 * @throws UserNotFoundException
	 */
	@GetMapping("/read-all-notes")
	public ResponseEntity<List<ViewNoteDTO>> readAllNotes()
			throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException {

		noteService.readAllNotes();

		return new ResponseEntity<>(noteService.readAllNotes(), HttpStatus.OK);
	}

	// -----------------Read Notes Of A Particular User------------------

	/**
	 * 
	 * @param req
	 * @return List of Notes of A Particular User
	 * @throws NullEntryException
	 * @throws NoteNotFoundException
	 * @throws NoteCreationException
	 * @throws UserNotFoundException
	 */
	@GetMapping("/read-user-notes")
	public ResponseEntity<List<ViewNoteDTO>> readUserNotes(HttpServletRequest req)
			throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException {

		String userId = (String) req.getAttribute("token");
		noteService.readUserNotes(userId);

		return new ResponseEntity<>(noteService.readUserNotes(userId), HttpStatus.OK);
	}

	// ----------Read A Particular Note-------------------------------

	/**
	 * 
	 * @param req
	 * @param noteId
	 * @return A Particular Note By Its Id
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	@PostMapping("/getnote/{noteId}")
	public ResponseEntity<ViewNoteDTO> readParticularNote(HttpServletRequest req, @PathVariable("noteId") String noteId)
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		String userId = (String) req.getAttribute("token");

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
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws DateException
	 */
	@RequestMapping(value = "/add-color/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<Response> addNoteColor(HttpServletRequest req, @RequestParam(value = "color") String color,
			@PathVariable String noteId) throws NoteCreationException, UserNotFoundException, NoteNotFoundException,
			NoteTrashedException, DateException {

		String userId = (String) req.getAttribute("token");

		noteService.addColor(userId, color, noteId);

		Response response = new Response();

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
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 * @throws DateException
	 */
	@RequestMapping(value = "/add-reminder/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<Response> addNoteReminder(HttpServletRequest req,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @PathVariable String noteId)
			throws NoteCreationException, UserNotFoundException, NoteNotFoundException, NoteTrashedException,
			DateException {

		String userId = (String) req.getAttribute("token");

		noteService.addReminder(userId, date, noteId);

		Response response = new Response();

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
	 * @throws NullEntryException
	 * @throws UserNotFoundException
	 * @throws NoteNotFoundException
	 * @throws NoteTrashedException
	 */
	@RequestMapping(value = "/delete-reminder/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<Response> deleteNoteReminder(HttpServletRequest req, @PathVariable String noteId)
			throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		String userId = (String) req.getAttribute("token");

		noteService.deleteReminder(userId, noteId);

		Response response = new Response();

		response.setMessage("Reminder is successfully removed!!");
		response.setStatus(9);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
