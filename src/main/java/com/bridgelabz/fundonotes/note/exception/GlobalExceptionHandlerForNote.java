package com.bridgelabz.fundonotes.note.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bridgelabz.fundonotes.note.controller.NoteController;
import com.bridgelabz.fundonotes.note.model.ResponseDTONote;

@ControllerAdvice
public class GlobalExceptionHandlerForNote {

	public static final Logger logger = LoggerFactory.getLogger(NoteController.class);

	@ExceptionHandler(NoteCreationException.class)
	public ResponseEntity<ResponseDTONote> noteExceptionHandler(NoteCreationException e) {
		logger.error("note entry error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("note entry error, " + e.getMessage());
		response.setStatus(101);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoteNotFoundException.class)
	public ResponseEntity<ResponseDTONote> noteNotFoundExceptionHandler(NoteNotFoundException e) {
		logger.error("note not found error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("note not found exception, " + e.getMessage());
		response.setStatus(201);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UnAuthorizedException.class)
	public ResponseEntity<ResponseDTONote> userNotFoundExceptionHandler(UnAuthorizedException e) {
		logger.error("UnAuthorized error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("UnAuthorized exception, " + e.getMessage());
		response.setStatus(301);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NullValueException.class)
	public ResponseEntity<ResponseDTONote> nullEntryExceptionHandler(NullValueException e) {
		logger.error("null value error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("null value exception, " + e.getMessage());
		response.setStatus(401);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UntrashedException.class)
	public ResponseEntity<ResponseDTONote> untrashedExceptionHandler(UntrashedException e) {
		logger.error("note untrashed error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("note untrashed exception, " + e.getMessage());
		response.setStatus(401);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoteTrashedException.class)
	public ResponseEntity<ResponseDTONote> trashedNoteExceptionHandler(NoteTrashedException e) {
		logger.error("note trashed error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("note trashed exception, " + e.getMessage());
		response.setStatus(601);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoRemiderToSetException.class)
	public ResponseEntity<ResponseDTONote> noReminderToSetExceptionHandler(NoRemiderToSetException e) {
		logger.error("no reminder to set error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("no reminder to set exception, " + e.getMessage());
		response.setStatus(701);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoteArchievedException.class)
	public ResponseEntity<ResponseDTONote> noteArchievedExceptionHandler(NoteArchievedException e) {
		logger.error("no reminder to set error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("no reminder to set exception, " + e.getMessage());
		response.setStatus(801);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NotePinnedException.class)
	public ResponseEntity<ResponseDTONote> notePinnedExceptionHandler(NotePinnedException e) {
		logger.error("no reminder to set error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("note pinned exception, " + e.getMessage());
		response.setStatus(901);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoSuchLabelException.class)
	public ResponseEntity<ResponseDTONote> noSuchLabelExceptionHandler(NoSuchLabelException e) {
		logger.error("no label to set error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("no label available to set exception, " + e.getMessage());
		response.setStatus(111);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(LabelCreationException.class)
	public ResponseEntity<ResponseDTONote> labelCreationExceptionHandler(LabelCreationException e) {
		logger.error("no label to set error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("no label available to set exception, " + e.getMessage());
		response.setStatus(112);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DateException.class)
	public ResponseEntity<ResponseDTONote> dateExceptionHandler(DateException e) {
		logger.error("date error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("date exception, " + e.getMessage());
		response.setStatus(113);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(LabelAdditionException.class)
	public ResponseEntity<ResponseDTONote> LabelAdditionExceptionHandler(LabelAdditionException e) {
		logger.error("label addition error in list");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("label addition exception, " + e.getMessage());
		response.setStatus(114);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(LabelNotFoundException.class)
	public ResponseEntity<ResponseDTONote> labelNotFoundExceptionHandler(LabelNotFoundException e) {
		logger.error("label not found error in list");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("label not found exception, " + e.getMessage());
		response.setStatus(116);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoteUnPinnedException.class)
	public ResponseEntity<ResponseDTONote> noteUnPinnedExceptionHandler(NoteUnPinnedException e) {
		logger.error("note unpinned error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("note unpinned exception, " + e.getMessage());
		response.setStatus(119);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoteUnArchievedException.class)
	public ResponseEntity<ResponseDTONote> noteUnArchievedExceptionHandler(NoteUnArchievedException e) {
		logger.error("note unarchieved error");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("note unarchieved exception, " + e.getMessage());
		response.setStatus(120);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MalFormedException.class)
	public ResponseEntity<ResponseDTONote> malformedExceptionHandler(MalFormedException e) {
		logger.error("malformed error jsoup");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("malformed exception jsoup, " + e.getMessage());
		response.setStatus(121);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UrlAdditionException.class)
	public ResponseEntity<ResponseDTONote> urlAdditionExceptionHandler(UrlAdditionException
			 e) {
		logger.error("malformed error jsoup");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("malformed exception jsoup, " + e.getMessage());
		response.setStatus(122);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(S3ClientException.class)
	public ResponseEntity<ResponseDTONote> s3ClientExceptioHandler(S3ClientException
			 e) {
		logger.error("s3Client exception");
		ResponseDTONote response = new ResponseDTONote();
		response.setMessage("s3Client exception, " + e.getMessage());
		response.setStatus(123);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(RestHighLevelClientException.class)
	public ResponseEntity<ResponseDTONote> restHighLevelClientExceptionHandler(RestHighLevelClientException e){
		logger.error("Rest high level client exception");
		ResponseDTONote response=new ResponseDTONote();
		response.setMessage("Rest high level client exception, "+e.getMessage());
		response.setStatus(1106);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	/*@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO> controller(Exception e) {
		logger.error("other exceptions");
		ResponseDTO response=new ResponseDTO();
		response.setMessage("Some exceptions occured, "+e.getMessage());
		response.setStatus(-1);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}*/
}
