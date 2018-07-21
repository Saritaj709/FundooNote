package com.bridgelabz.fundonotes.note.controller;

import java.util.Date;
import java.util.List;

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

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NullEntryException;
import com.bridgelabz.fundonotes.note.exception.UntrashedException;
import com.bridgelabz.fundonotes.note.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.NoteDTO;
import com.bridgelabz.fundonotes.note.model.Response;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.model.ViewDTO;
import com.bridgelabz.fundonotes.note.services.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	@Autowired
	NoteService noteService;

	// -------------Create A New Note----------------------

	@PostMapping(value="/create")
	public ResponseEntity<Response> createNote(@RequestParam String token, @RequestBody CreateDTO createDto)
			throws NoteCreationException, NoteNotFoundException, UserNotFoundException {

		noteService.createNote(token, createDto);

		Response response = new Response();

		response.setMessage("Congratulations,your note is successfully created");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ------------Update An Existing Note-------------------

	@PutMapping(value="/update/{noteId}")
	public ResponseEntity<Response> updateNote(@RequestParam String token, @RequestBody UpdateDTO updateDto,
			@PathVariable String noteId) throws NoteNotFoundException, UserNotFoundException, NoteTrashedException {

		noteService.updateNote(token, updateDto, noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your details are successfully updated");
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------Move An Existing Note To Trash---------------

	@PostMapping(value="/delete/{noteId}")
	public ResponseEntity<Response> deleteNote(@RequestParam String token,@PathVariable
			String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		noteService.trashNote(token,noteId);

		Response response = new Response();

		response.setMessage("Congratulations, your note is successfully moved to trash");
		response.setStatus(3);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	//--------------------Restore A Note From Trash------------
	
	@PostMapping(value="/restore/{noteId}")
	public ResponseEntity<Response> restoreNote(@RequestParam String token,@PathVariable
			String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		noteService.restoreNote(token,noteId);

		Response response = new Response();

		response.setMessage("Congratulations, your note is successfully restored");
		response.setStatus(10);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ----------Delete An Existing Note From Trash--------------

	@DeleteMapping(value="/deleteForever/{noteId}")
	public ResponseEntity<Response> deleteNoteForever(@RequestParam String token,
			@PathVariable String noteId) throws NoteNotFoundException, UserNotFoundException, UntrashedException, NoteTrashedException {

		noteService.deleteNoteForever(token,noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your details are successfully deleted");
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------Read All Existing Notes--------------------------

	@GetMapping("/readAll")
	public ResponseEntity<List<ViewDTO>> readNote() throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException {

		 noteService.readNotes();
		
		return new ResponseEntity<>(noteService.readNotes(), HttpStatus.OK);
	}

	//----------------Read Entire Details Of All The Notes------------
	
	@GetMapping("/readAllNotes")
	public ResponseEntity<List<NoteDTO>> readAllNotes() throws NullEntryException, NoteNotFoundException, NoteCreationException, UserNotFoundException {

		 noteService.readAllNotes();
		
		return new ResponseEntity<>(noteService.readAllNotes(), HttpStatus.OK);
	}

	// ----------Read A Particular Note-------------------------------

	@PostMapping("/readOneNote/{noteId}")
	public ResponseEntity<ViewDTO> readParticularNote(@RequestParam String token, @PathVariable("noteId") String noteId
		) throws UserNotFoundException, NoteNotFoundException, NoteTrashedException  {

		 noteService.findNoteById(token, noteId);

		 return new ResponseEntity<>(noteService.findNoteById(token, noteId),HttpStatus.OK);
	}

	// ---------Add A Reminder To A Particular Note---------------------

	@RequestMapping(value = "/addReminder/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<Response> addNoteReminder(@RequestParam String token,@RequestParam
			@DateTimeFormat(pattern="yyyy-MM-dd") Date date, @PathVariable String noteId) throws NoteCreationException, UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		noteService.addReminder(token,date, noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your reminder is successfully set!!");
		response.setStatus(7);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// -------Delete Reminder From A Note---------------------------------

	@RequestMapping(value = "/deleteReminder/{noteId}", method = RequestMethod.POST)
	public ResponseEntity<Response> deleteNoteReminder(@RequestParam String token,@PathVariable String noteId) throws NullEntryException, UserNotFoundException, NoteNotFoundException, NoteTrashedException {

		noteService.deleteReminder(token,noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your reminder is successfully removed!!");
		response.setStatus(9);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
