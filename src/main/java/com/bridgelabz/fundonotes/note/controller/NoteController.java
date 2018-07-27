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

	@PostMapping(value = "/create")
	public ResponseEntity<Note> createNote(HttpServletRequest req,
			@RequestBody CreateDTO createDto)
			throws NoteCreationException, NoteNotFoundException, UserNotFoundException, DateException, LabelNotFoundException, NullEntryException {

		String userId= (String) req.getAttribute("token");
		
		Note note=noteService.createNote(userId, createDto);

		return new ResponseEntity<>(note, HttpStatus.CREATED);
	}

	// ------------------Add A Label--------------------------

	@PostMapping(value = "/createLabel")
	public ResponseEntity<Label> createLabelInsideNote(HttpServletRequest req,
			@RequestBody CreateLabelDTO createLabelDto)
			throws NoteCreationException, NoteNotFoundException, UserNotFoundException, NullEntryException {

		String userId=(String) req.getAttribute("token");
		
		Label label=noteService.createLabel(userId, createLabelDto);

		return new ResponseEntity<>(label, HttpStatus.CREATED);
	}

	// ------------------View All Labels---------------------------

	@GetMapping(value = "/viewalllabels")
	public ResponseEntity<List<Label>> viewAllLabels()
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, NullEntryException {

		noteService.viewLabels();

		return new ResponseEntity<>(noteService.viewLabels(), HttpStatus.OK);
	}

	//-----------------View Label-------------------------------
	@GetMapping(value = "/viewlabel")
	public ResponseEntity<List<ViewNoteDTO>> viewLabel(HttpServletRequest req,@RequestParam(value="LabelId")String labelId)
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, NullEntryException, LabelNotFoundException {

		String userId=(String) req.getAttribute("token");
		noteService.viewLabel(userId,labelId);

		return new ResponseEntity<>(noteService.viewLabel(userId,labelId), HttpStatus.OK);
	}
	
	// ---------------Add Label To Notes-----------------------
	
	@PostMapping(value = "/addlabel/{noteId}")
	public ResponseEntity<Response> addLabelToNotes(HttpServletRequest req,
			@RequestParam(value = "labelName") String labelName, @PathVariable(value = "noteId") String noteId)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException, LabelAdditionException {

		String userId=(String) req.getAttribute("token");
		
		noteService.addLabel(userId, labelName, noteId);

		Response response = new Response();

		response.setMessage("Label is successfully added");
		response.setStatus(15);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	//---------------Edit Or Delete A Label-----------------
	
	@PostMapping(value = "/editordeleteLabel")
	public ResponseEntity<Response> editOrDeleteLabel(HttpServletRequest req,
			@RequestBody Label labelDto,@RequestParam(value="choice,edit/delete")String choice)
			throws Exception {

		String userId=(String) req.getAttribute("token");
		
		noteService.editOrRemoveLabel(userId,labelDto,choice);

		Response response = new Response();

		response.setMessage("Label is successfully updated");
		response.setStatus(16);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	//-------------Delete A Label------------------------------
	
	@DeleteMapping(value = "/deleteLabel")
	public ResponseEntity<Response> deleteLabel(HttpServletRequest req,
			@RequestParam(value="Label Id")String labelId)
			throws Exception {

		String userId=(String) req.getAttribute("token");
		
		noteService.removeLabel(userId,labelId);

		Response response = new Response();

		response.setMessage("Label is successfully updated");
		response.setStatus(17);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	//-------------Update A Label-----------------------------
	
	@PutMapping(value = "/editLabel")
	public ResponseEntity<Response> editLabel(HttpServletRequest req,
			@RequestParam(value="Label Id")String labelId,@RequestParam(value="editName")String labelName)
			throws Exception {

		String userId=(String) req.getAttribute("token");
		
		noteService.editLabel(userId,labelId,labelName);

		Response response = new Response();

		response.setMessage("Label is successfully updated");
		response.setStatus(19);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	
	// ------------Update An Existing Note-------------------

	@PutMapping(value = "/update")
	public ResponseEntity<Response> updateNote(HttpServletRequest req,
			@RequestBody UpdateDTO updateDto)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {

		String userId=(String) req.getAttribute("token");
		
		noteService.updateNote(userId, updateDto);

		Response response = new Response();

		response.setMessage("Note successfully updated");
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// --------------------View trashed Notes--------------------

	@GetMapping("/viewtrashed")
	public ResponseEntity<List<ViewNoteDTO>> viewTrashedNotes()
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException, NullEntryException {

		noteService.viewTrashed();

		return new ResponseEntity<>(noteService.viewTrashed(), HttpStatus.OK);
	}
	
	//------------------Move To Trash Or Restore A Note-----------------------
	
	@PostMapping(value = "/deleteorrestore/{noteId}")
	public ResponseEntity<Response> deleteOrRestoreNote(HttpServletRequest req,
			@PathVariable String noteId,@RequestParam(value="choice,delete/restore")String choice)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {


		String userId=(String) req.getAttribute("token");

		noteService.deleteOrRestoreNote(userId, noteId,choice);

		Response response = new Response();

		response.setMessage("Note is successfully restored/trashed");
		response.setStatus(117);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ----------Delete An Existing Note From Trash--------------

	@DeleteMapping(value = "/deleteforever/{noteId}")
	public ResponseEntity<Response> deleteNoteForever(@PathVariable String noteId,
			HttpServletRequest req)
			throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		String userId=(String) req.getAttribute("token");
		
		noteService.deleteNoteForever(userId, noteId);

		Response response = new Response();

		response.setMessage("Note successfully deleted");
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------------To Archieve Notes----------------------------

	@PostMapping(value = "/archieve/{noteId}")
	public ResponseEntity<Response> archieveNote(@PathVariable String noteId,
			HttpServletRequest req)
			throws NoteNotFoundException, UserNotFoundException, NoteTrashedException, NoteArchievedException {

		String userId=(String) req.getAttribute("token");
		
		noteService.archieveNote(userId, noteId);

		Response response = new Response();

		response.setMessage("Note is successfully moved to trash");
		response.setStatus(11);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// --------------View Archieved Notes--------------------------------

	@GetMapping(value = "/viewarchieved")
	public ResponseEntity<List<ViewNoteDTO>> viewArchievedNotes() throws NullEntryException {

		noteService.viewArchieved();

		return new ResponseEntity<>(noteService.viewArchieved(), HttpStatus.OK);
	}
	
	// -------------------To Pin Notes---------------------------------

	@PostMapping(value = "/pin/{noteId}")
	public ResponseEntity<Response> pinNote(@PathVariable String noteId,HttpServletRequest req)
			throws NoteNotFoundException, UserNotFoundException, NotePinnedException, NoteTrashedException {

		String userId=(String) req.getAttribute("token");
		
		noteService.pinNote(userId, noteId);

		Response response = new Response();

		response.setMessage("Note is successfully moved to trash");
		response.setStatus(12);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------------View Pinned Notes----------------------------

	@GetMapping(value = "/viewpinned")
	public ResponseEntity<List<ViewNoteDTO>> viewPinnedNotes() throws NullEntryException {

		noteService.viewPinned();

		return new ResponseEntity<>(noteService.viewPinned(), HttpStatus.OK);
	}

	// ----------------Read Entire Details Of All The Notes------------

	@GetMapping("/readallnotes")
	public ResponseEntity<List<ViewNoteDTO>> readAllNotes()
			throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException {

		noteService.readAllNotes();

		return new ResponseEntity<>(noteService.readAllNotes(), HttpStatus.OK);
	}

	// ----------Read A Particular Note-------------------------------

	@PostMapping("/getnote/{noteId}")
	public ResponseEntity<ViewNoteDTO> readParticularNote(HttpServletRequest req,
			@PathVariable("noteId") String noteId)
			throws UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		String userId=(String) req.getAttribute("token");
		
		noteService.findNoteById(userId, noteId);

		return new ResponseEntity<>(noteService.findNoteById(userId, noteId), HttpStatus.OK);
	}

	// ---------Add A Reminder To A Particular Note---------------------

	@RequestMapping(value = "/addreminder/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<Response> addNoteReminder(HttpServletRequest req,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @PathVariable String noteId)
			throws NoteCreationException, UserNotFoundException, NoteNotFoundException, NoteTrashedException, DateException {

		String userId=(String) req.getAttribute("token");
		
		noteService.addReminder(userId, date, noteId);

		Response response = new Response();

		response.setMessage("Reminder is successfully set!!");
		response.setStatus(7);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// -------Delete Reminder From A Note---------------------------------

	@RequestMapping(value = "/deletereminder/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<Response> deleteNoteReminder(HttpServletRequest req,
			@PathVariable String noteId)
			throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		String userId=(String) req.getAttribute("token");
		
		noteService.deleteReminder(userId, noteId);

		Response response = new Response();

		response.setMessage("Reminder is successfully removed!!");
		response.setStatus(9);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
