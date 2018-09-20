package com.bridgelabz.fundonotes.user.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bridgelabz.fundonotes.user.controller.UserController;
import com.bridgelabz.fundonotes.user.model.ResponseDTOUser;

@ControllerAdvice
public class GlobalExceptionHandler {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@ExceptionHandler(RegistrationException.class)
	public ResponseEntity<ResponseDTOUser> register(RegistrationException e){
		logger.error("Registration exception");
		ResponseDTOUser response=new ResponseDTOUser();
		response.setMessage("Registration error, "+e.getMessage());
		response.setStatus(1101);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(LoginException.class)
	public ResponseEntity<ResponseDTOUser> login(LoginException e){
		logger.error("Login exception");
		ResponseDTOUser response=new ResponseDTOUser();
		response.setMessage("Login error, "+e.getMessage());
		response.setStatus(1102);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseDTOUser> userNotFoundExceptionHandler(UserNotFoundException e){
		logger.error("User not found exception");
		ResponseDTOUser response=new ResponseDTOUser();
		response.setMessage("User not found Exception, "+e.getMessage());
		response.setStatus(1103);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ActivationException.class)
	public ResponseEntity<ResponseDTOUser> activationExceptionHandler(ActivationException e){
		logger.error("User account activation exception exception");
		ResponseDTOUser response=new ResponseDTOUser();
		response.setMessage("User account activation Exception, "+e.getMessage());
		response.setStatus(1104);
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
