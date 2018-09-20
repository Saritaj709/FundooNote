package com.bridgelabz.fundonotes.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.RestHighLevelClientException;
import com.bridgelabz.fundonotes.user.exception.ActivationException;
import com.bridgelabz.fundonotes.user.exception.LoginException;
import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;
import com.bridgelabz.fundonotes.user.model.Response;
import com.bridgelabz.fundonotes.user.model.ResponseDTOUser;
import com.bridgelabz.fundonotes.user.model.User;
import com.bridgelabz.fundonotes.user.services.UserDao;
import com.bridgelabz.fundonotes.user.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	/*@Autowired
	private UserDao userService;
	*/
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	// -------------Get All Users--------------------------

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	//--------------Get User By Id----------------------------
	@RequestMapping(value = "/get-user-by-id/{id}", method = RequestMethod.POST)
	public ResponseEntity<Object> getAllUserById(@PathVariable(value="id")String id) throws UserNotFoundException, NullValueException, RestHighLevelClientException, IOException{
		userService.getUserById(id);
		return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
	}

	//--------------Get User By Email----------------------------
	@RequestMapping(value = "/get-user-by-email/{email}", method = RequestMethod.POST)
	public ResponseEntity<Object> getAllUserByEmail(@PathVariable(value="email")String email) throws UserNotFoundException, NullValueException, RestHighLevelClientException, IOException{
		userService.getUserById(email);
		return new ResponseEntity<>(userService.getUserById(email), HttpStatus.OK);
	}

	// ----------------Activate USer Using RequestParam------------

	@RequestMapping(value = "/activate", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTOUser> activateAcc(@RequestParam(value = "token") String token)
			throws RegistrationException, UserNotFoundException {

		userService.activate(token);

		ResponseDTOUser response = new ResponseDTOUser();

		response.setMessage("Account activated successfully");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// -----------------------Registration------------------------

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Response> registerUser(@RequestBody RegistrationDTO user) throws RegistrationException, IOException, RestHighLevelClientException {

		userService.registerUser(user);

		Response response = new Response("User with given email registered successfully",1);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ------------------------Delete a User-------------------

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseDTOUser> deleteUser(@RequestParam String email) throws UserNotFoundException {

		ResponseDTOUser response = new ResponseDTOUser();

		userService.deleteUser(email);

		response.setMessage("User with email id " + email + " successfully deleted");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------------------------Update User-----------------------

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public ResponseEntity<ResponseDTOUser> updateUser(@RequestBody User user) throws UserNotFoundException {

		userService.updateUser(user);

		ResponseDTOUser response = new ResponseDTOUser();
		response.setMessage("User with email " + user.getId() + " successfully updated");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	// --------------------------Login User-------------------------

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Response> loginUser(@RequestBody LoginDTO user, HttpServletResponse res)
			throws LoginException, UserNotFoundException, ActivationException, IOException, RestHighLevelClientException {

			String token=userService.loginUser(user);
			res.setHeader("token", token);

		Response response = new Response("User Successfully logged in",2);	
			return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// -----------------------Forgot password------------------------

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTOUser> forgetPassword(@RequestParam(value = "email") String email)
			throws UserNotFoundException {

		userService.forgetPassword(email);
		System.out.println(email);
		ResponseDTOUser response = new ResponseDTOUser();
		response.setMessage("link sent to email,pls check and verify");
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ----------------------Reset password----------------------------

	@RequestMapping(value = "/resetpassword", method = RequestMethod.PUT)
	public ResponseEntity<Response> resetPassword(@RequestHeader(value = "token") String token,
			@RequestBody PasswordDTO passwordDto) throws Exception {

		userService.passwordReset(token, passwordDto);

		Response response = new Response("Password is successfully changed",4);
	
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------------------Upload User Profile Picture-------------------

	@RequestMapping(value = "/upload-profilepic", method = RequestMethod.POST)
	public ResponseEntity<?> uploadPic(HttpServletRequest req, @RequestParam(value = "file") MultipartFile multipartFile)
			throws UserNotFoundException, IOException, RestHighLevelClientException {
		String userId = (String) req.getAttribute("userId");
		String profilePic = userService.uploadPic(userId, multipartFile);
		return new ResponseEntity<>(profilePic, HttpStatus.OK);
	}

	// ---------------------Remove User Profile Picture----------------------

	@RequestMapping(value = "/remove-profilepic", method = RequestMethod.DELETE)
	public ResponseEntity<String> removePic(HttpServletRequest req) throws UserNotFoundException, IOException, NullValueException, RestHighLevelClientException {
		String userId = (String) req.getAttribute("userId");
		String profilePic = userService.removePic(userId);
		return new ResponseEntity<>(profilePic, HttpStatus.OK);
	}
}
