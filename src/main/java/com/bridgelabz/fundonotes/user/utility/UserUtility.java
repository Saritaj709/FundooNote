package com.bridgelabz.fundonotes.user.utility;

import java.util.UUID;

import com.bridgelabz.fundonotes.user.exception.LoginException;
import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;

public class UserUtility {

	private final static String EMAIL = "^\\w+@\\w+\\..{2,3}(.{2,3})?$";
	private final static String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,32}$";

	public static void validateUser(RegistrationDTO user) throws RegistrationException {
		if (!user.getEmail().matches(EMAIL) || user.getEmail() == null) {
			throw new RegistrationException("User email is not valid ,follow abc@gmail.com,abc.100@yahoo.com");
		} else if (user.getFirstname().length() < 3) {
			throw new RegistrationException("User Firstname should have atleast 3 characters");
		} else if (user.getLastname().length() < 3) {
			throw new RegistrationException("User Lastname should have atleast 3 characters ");
		}

		else if (user.getPhoneNo().length() != 10) {
			throw new RegistrationException("contact no. is invalid");

		} else if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {

			throw new RegistrationException("Password is invalid,should have 8-32 characters");
		} else if (!user.getPassword().matches(PASSWORD)) {
			throw new RegistrationException("Invalid Password Format");
		}

		else if (!user.getConfirmPassword().equals(user.getPassword())) {
			throw new RegistrationException("Password is invalid ,both password should be same");
		}
	}

	public static void validateLogin(LoginDTO loginDto) throws LoginException {

		if (loginDto.getEmail().length() == 0 && loginDto.getPassword().length() == 0) {
			throw new LoginException("Both email and password is null");
		}
		if (loginDto.getEmail().length() == 0) {
			throw new LoginException("email is null");
		}

		if (!loginDto.getEmail().matches(EMAIL)) {
			throw new LoginException("Email format is not valid");
		}

		if (loginDto.getPassword().length() == 0) {
			throw new LoginException("Password is null");
		}
	}

	public static void validateReset(PasswordDTO passwordDto) throws RegistrationException {

		if (!passwordDto.getPassword().equals(passwordDto.getConfirmPassword())) {
			throw new RegistrationException("Passwords should be same");
		}
	}

	public static void validateEmail(String email) throws LoginException {
		if (!email.matches(EMAIL)) {
			throw new LoginException("Invalid email format");
		}
	}

	public static String generateUUId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
