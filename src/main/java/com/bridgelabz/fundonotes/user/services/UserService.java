package com.bridgelabz.fundonotes.user.services;

import java.util.List;

import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;
import com.bridgelabz.fundonotes.user.model.User;

public interface UserService {
	public List<User> getAllUsers();

	public String saveUser(RegistrationDTO user) throws RegistrationException;

	public void getUserById(String id);

	public String loginUser(LoginDTO loginDto);

	public String updateUser(User user);

	public void deleteUser(String email);

	public boolean activateJwt(String token);

	public void forgetPassword(String id,String email);
	
	public void passwordReset(String token,PasswordDTO dto) throws RegistrationException, Exception;

	boolean activate(String token, String id);

}
