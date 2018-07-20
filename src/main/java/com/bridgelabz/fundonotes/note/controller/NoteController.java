package com.bridgelabz.fundonotes.note.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.model.CreateDTO;
import com.bridgelabz.fundonotes.note.model.Response;
import com.bridgelabz.fundonotes.note.model.UpdateDTO;
import com.bridgelabz.fundonotes.note.services.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

	@Autowired
	NoteService noteService;

	// -------------Create A New Note----------------------

	@PostMapping("/create")
	public ResponseEntity<Response> createNote(@RequestParam String token, @RequestBody CreateDTO create)
			throws NoteCreationException {

		noteService.createNote(token, create);

		Response response = new Response();

		response.setMessage("Congratulations,your note is successfully created");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ------------Update An Existing Note-------------------

	@PutMapping(value="/update")
	public ResponseEntity<Response> updateNote(@RequestParam String token, @RequestBody UpdateDTO update,
			@RequestParam String noteId) throws NoteCreationException {

		noteService.updateNote(token, update, noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your details are successfully updated");
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------Move An Existing Note To Trash---------------

	@PostMapping("/trash")
	public ResponseEntity<Response> moveNoteToTrash(@RequestParam String token, @RequestParam String userId,@RequestParam
			String noteId) throws NoteCreationException {

		noteService.moveNoteToTrash(token, userId, noteId);

		Response response = new Response();

		response.setMessage("Congratulations, your note is successfully moved to trash");
		response.setStatus(3);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ----------Delete An Existing Note From Trash--------------

	@DeleteMapping(value="/delete")
	public ResponseEntity<Response> deleteNote(@RequestParam String token, @RequestParam String userId,
			@RequestParam String noteId) throws NoteCreationException {

		noteService.deleteNote(token, userId, noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your details are successfully deleted");
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------Read All Existing Notes--------------------------

	@GetMapping("/readAll")
	public ResponseEntity<Response> readNote() throws NoteCreationException {

		noteService.readAllNotes();

		Response response = new Response();

		response.setMessage("The details are fetched for all the notes,you can kindly have a look");
		response.setStatus(5);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ----------Read A Particular Note-------------------------------

	@GetMapping("/readOne")
	public ResponseEntity<Response> readParticularNote(@RequestParam String token, @RequestParam String noteId,
			@RequestParam String userId) throws NoteCreationException {

		noteService.findNoteById(token, noteId, userId);

		Response response = new Response();

		response.setMessage("The details are fetched for all the notes,you can kindly have a look");
		response.setStatus(6);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------Add A Reminder To A Particular Note---------------------

	@RequestMapping(value = "/addReminder", method = RequestMethod.POST)
	public ResponseEntity<Response> addNoteReminder(@RequestParam String token, @RequestParam String userId,
			@RequestParam Date date, @RequestParam String noteId) throws NoteCreationException {

		noteService.addReminder(token, userId, date, noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your reminder is successfully set!!");
		response.setStatus(7);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// -------Delete Reminder From A Note---------------------------------

	@RequestMapping(value = "/deleteReminder", method = RequestMethod.POST)
	public ResponseEntity<Response> deleteNoteReminder(@RequestParam String token, @RequestParam String userId,
			@RequestParam Date date, @RequestParam String noteId) throws NoteCreationException {

		noteService.deleteReminder(token, userId, date, noteId);

		Response response = new Response();

		response.setMessage("Congratulations,your reminder is successfully removed!!");
		response.setStatus(9);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
