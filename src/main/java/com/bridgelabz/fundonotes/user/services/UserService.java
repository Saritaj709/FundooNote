package com.bridgelabz.fundonotes.user.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.user.exception.ActivationException;
import com.bridgelabz.fundonotes.user.exception.LoginException;
import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;
import com.bridgelabz.fundonotes.user.model.User;

public interface UserService {
	public List<User> getAllUsers();

	public String registerUser(RegistrationDTO user) throws RegistrationException, IOException;

	public User getUserById(String id) throws UserNotFoundException;

	public String loginUser(LoginDTO loginDto) throws LoginException, UserNotFoundException, ActivationException;

	public String updateUser(User user) throws UserNotFoundException;

	public void deleteUser(String email) throws UserNotFoundException;

	public void activateJwt(String token);

	public void forgetPassword(String id) throws UserNotFoundException;
	
	public void passwordReset(String token,PasswordDTO dto) throws RegistrationException, Exception;

	void activate(String token) throws UserNotFoundException;

	public String uploadPic(String token, MultipartFile multipartFile) throws UserNotFoundException, IOException;

	public String removePic(String token) throws UserNotFoundException, IOException, NullValueException;

}
