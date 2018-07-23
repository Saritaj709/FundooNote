package com.bridgelabz.fundonotes.user.services;

import java.util.List;

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

	public String registerUser(RegistrationDTO user) throws RegistrationException;

	public void getUserById(String id) throws UserNotFoundException;

	public String loginUser(LoginDTO loginDto) throws LoginException, UserNotFoundException, ActivationException;

	public String updateUser(User user) throws UserNotFoundException;

	public void deleteUser(String email) throws UserNotFoundException;

	public boolean activateJwt(String token);

	public void forgetPassword(String id) throws UserNotFoundException;
	
	public void passwordReset(String token,PasswordDTO dto) throws RegistrationException, Exception;

	boolean activate(String token) throws UserNotFoundException;

}
