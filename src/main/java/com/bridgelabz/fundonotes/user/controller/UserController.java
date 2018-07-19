package com.bridgelabz.fundonotes.user.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;
import com.bridgelabz.fundonotes.user.model.ResponseDTO;
import com.bridgelabz.fundonotes.user.model.User;
import com.bridgelabz.fundonotes.user.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	//-------------Get All Users--------------------------
	
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	//-----------Activate account for registration----------------
	
	@RequestMapping(value = "/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<ResponseDTO> activateAccount(HttpServletRequest request) throws RegistrationException {

		String token = request.getQueryString();
		System.out.println(token);

		ResponseDTO response = new ResponseDTO();

		if (userService.activateJwt(token)) {

			response.setMessage("Account activated successfully");
			response.setStatus(1);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {

			response.setMessage("Account not yet activated");
			response.setStatus(-1);

			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
		}
	}

	//-----------------------Registration------------------------
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO> registerUser(@RequestBody RegistrationDTO user) throws RegistrationException {

		userService.saveUser(user);

		ResponseDTO response = new ResponseDTO();
		response.setMessage("User with email " + user.getEmail() + " registered successfully");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	//------------------------Delete a User-------------------
	
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseDTO> deleteUser(@RequestBody User user) {

		ResponseDTO response = new ResponseDTO();

		/*if (userService.getUserByEmail(user.getEmail()) == false) {

			response.setMessage("User with email " + user.getEmail() + " exists");
			response.setStatus(-1);

			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}*/
		userService.deleteUser(user.getEmail());

		response.setMessage("User with email " + user.getEmail() + " successfully deleted");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	//---------------------------Update User-----------------------
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public ResponseEntity<ResponseDTO> updateUser(@RequestBody User user) {

		userService.updateUser(user);

		ResponseDTO response = new ResponseDTO();
		response.setMessage("User with email " + user.getEmail() + " successfully updated");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
     //--------------------------Login User-------------------------
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginDTO user, HttpServletRequest res) {
		userService.loginUser(user);

		ResponseDTO response = new ResponseDTO();
		response.setMessage("User with email " + user.getEmail() + ", Sucessfully logged in");
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	//-----------------------Forgot password------------------------
	
	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO> forgetPassword(@RequestParam(value = "email") String email) {

		userService.forgetPassword(email);

		ResponseDTO response = new ResponseDTO();
		response.setMessage("link sent to email,pls check and verify");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
    //----------------------Reset password----------------------------
	
	@RequestMapping(value = "/resetpassword", method = RequestMethod.PUT)
	public ResponseEntity<ResponseDTO> resetPassword(@RequestParam(value = "token") String token,
			@RequestBody PasswordDTO passwordDto) throws Exception {

		userService.passwordReset(token, passwordDto);

		ResponseDTO response = new ResponseDTO();
		response.setMessage("Congratulations,your password is successfully changed");
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
