package com.bridgelabz.fundonotes.note.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundonotes.note.exception.LabelAdditionException;
import com.bridgelabz.fundonotes.note.exception.LabelNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NoteCreationException;
import com.bridgelabz.fundonotes.note.exception.NoteNotFoundException;
import com.bridgelabz.fundonotes.note.exception.NoteTrashedException;
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.UnAuthorizedException;
import com.bridgelabz.fundonotes.note.model.Label;
import com.bridgelabz.fundonotes.note.model.LabelDTO;
import com.bridgelabz.fundonotes.note.model.Response;
import com.bridgelabz.fundonotes.note.model.ViewNoteDTO;
import com.bridgelabz.fundonotes.note.services.LabelService;

/**
 * @author bridgelabz
 *
 */
@RestController
@RequestMapping("/api/labels")
public class LabelController {

	@Autowired
	private LabelService labelService;
	
	// ------------------Create A Label--------------------------

		/**
		 * 
		 * @param req
		 * @param labelName
		 * @return Label
		 * @throws NoteCreationException
		 * @throws NoteNotFoundException
		 * @throws UnAuthorizedException
		 * @throws NullValueException
		 */
		@PostMapping(value = "/create")
		public ResponseEntity<LabelDTO> createLabelInsideNote(HttpServletRequest req,
				@RequestParam(value="labelName") String labelName)
				throws NoteCreationException, NoteNotFoundException, UnAuthorizedException, NullValueException {

			String userId = (String) req.getAttribute("userId");
            
			LabelDTO labelDto = labelService.createLabel(userId, labelName);

			return new ResponseEntity<>(labelDto, HttpStatus.CREATED);
		}

		// ------------------View All Labels---------------------------

		/**
		 * 
		 * @return List of Labels
		 * @throws UnAuthorizedException
		 * @throws NoteNotFoundException
		 * @throws NoteTrashedException
		 * @throws NullValueException
		 */
		@GetMapping(value = "/view-all-labels")
		public ResponseEntity<List<Label>> viewAllLabels()
				throws UnAuthorizedException, NoteNotFoundException, NoteTrashedException, NullValueException {

			labelService.viewLabels();

			return new ResponseEntity<>(labelService.viewLabels(), HttpStatus.OK);
		}
		
		//-------------------View Labels Of A Particular Label--------------------------
		
		@GetMapping(value="/view-user-labels")
		public ResponseEntity<List<LabelDTO>> viewUserLabels(HttpServletRequest req) throws NullValueException {
			
			String userId=(String) req.getAttribute("userId");
			
			labelService.viewUserLabels(userId);
			return new ResponseEntity<>(labelService.viewUserLabels(userId),HttpStatus.OK);
		}
		

		// -----------------View Label,Shows All Notes Containing This Label-------------------------------

		/**
		 * 
		 * @param req
		 * @param labelId
		 * @return Label of A Particular User
		 * @throws UnAuthorizedException
		 * @throws NoteNotFoundException
		 * @throws NoteTrashedException
		 * @throws NullValueException
		 * @throws LabelNotFoundException
		 */
		@GetMapping(value = "/view-label/{labelId}")
		public ResponseEntity<List<ViewNoteDTO>> viewLabel(HttpServletRequest req,
				@PathVariable(value = "labelId") String labelId) throws UnAuthorizedException, NoteNotFoundException,
				NoteTrashedException, NullValueException, LabelNotFoundException {

			String userId = (String) req.getAttribute("userId");
			labelService.viewLabel(userId, labelId);

			return new ResponseEntity<>(labelService.viewLabel(userId, labelId), HttpStatus.OK);
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
		 */
		@PostMapping(value = "/add-label/{noteId}")
		public ResponseEntity<Response> addLabelToNotes(HttpServletRequest req,
				@RequestParam(value = "labelId") String labelId, @PathVariable(value = "noteId") String noteId)
				throws NoteNotFoundException, UnAuthorizedException, NoteTrashedException, LabelAdditionException {

			String userId = (String) req.getAttribute("userId");

			labelService.addLabel(userId, labelId, noteId);

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

			String userId = (String) req.getAttribute("userId");

			labelService.removeLabel(userId, labelId);

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
				@RequestParam(value = "noteId") String noteId, @PathVariable(value = "labelId") String labelId)
				throws Exception {

			String userId = (String) req.getAttribute("userId");

			labelService.removeLabelFromNote(userId, noteId, labelId);

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
		@PutMapping(value = "/update-label/{labelId}")
		public ResponseEntity<Response> updateLabel(HttpServletRequest req, @PathVariable(value = "labelId") String labelId,
				@RequestParam(value = "updateNameTo") String labelName) throws Exception {

			String userId = (String) req.getAttribute("userId");

			labelService.editLabel(userId, labelId, labelName);

			Response response = new Response();

			response.setMessage("Label is successfully updated");
			response.setStatus(19);

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
		
	//--------------Sort Label By Date Or Name------------------------	
		/**
		 * @param req
		 * @param order
		 * @return LabelDTO in sorted order
		 * @throws NullValueException
		 */
		@PostMapping(value = "/sort-labels-by-date-or-name")
		public ResponseEntity<List<LabelDTO>> sortLabelsByDateOrName(HttpServletRequest req,@RequestParam(value="order,asc/desc",required=false)String order,@RequestParam(value="sortBy,date/name",required=false)String choice) throws NullValueException {

			String userId=(String) req.getAttribute("userId");
			labelService.sortLabelsByDateOrName(userId,order,choice);

			return new ResponseEntity<>(labelService.sortLabelsByDateOrName(userId,order,choice), HttpStatus.OK);
		}
		
		/**
		 * @param req
		 * @param order
		 * @return LabelDTO in sorted order
		 * @throws NullValueException
		 */
		@PostMapping(value = "/sort-labels-by-Date")
		public ResponseEntity<List<LabelDTO>> sortLabelsByDate(HttpServletRequest req,@RequestParam(value="order,asc/desc")String order) throws NullValueException {

			String userId=(String) req.getAttribute("userId");
			labelService.sortLabelsByDate(userId,order);

			return new ResponseEntity<>(labelService.sortLabelsByDate(userId,order), HttpStatus.OK);
		}
}
