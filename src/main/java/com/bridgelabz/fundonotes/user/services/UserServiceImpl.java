package com.bridgelabz.fundonotes.user.services;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.user.exception.LoginException;
import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.MailDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;
import com.bridgelabz.fundonotes.user.model.User;
import com.bridgelabz.fundonotes.user.utility.UserUtility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository; // extends mongoRepository

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private ProducerService producer;
	
	@Autowired
	private JwtToken jwtToken;

	@Override
	public List<User> getAllUsers() {

		List<User> userList = userRepository.findAll();
		return userList;
	}

	@Override
	public String saveUser(RegistrationDTO dto) throws RegistrationException {

		UserUtility.validateUser(dto);

		Optional<User> checkUser = userRepository.findByEmail(dto.getEmail());

		if (checkUser.isPresent()) {
			throw new RegistrationException("email id already exists,unable to register");

		}
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setFirstname(dto.getFirstname());
		user.setLastname(dto.getLastname());
		user.setPhoneNo(dto.getPhoneNo());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		userRepository.insert(user);

		String jwt = jwtToken.tokenGenerator(dto.getEmail());
		
		jwtToken.parseJwtToken(jwt);

		MailDTO mail = new MailDTO();
		mail.setTo(dto.getEmail());
		mail.setSubject("Account activation mail");
		mail.setText("Click here to verify your account:\n\n" + "http://192.168.0.73:8080/user/activateaccount/?"
				+ jwt);
		producer.sender(mail);

		return jwt;
	}

	@Override
	public void getUserByEmail(String email) {

		Optional<User> checkUser = userRepository.findByEmail(email);
		if (!checkUser.isPresent()) {
			throw new LoginException("User is not available");
		}
	}

	@Override
	public String loginUser(LoginDTO loginDto) throws LoginException {

		UserUtility.validateLogin(loginDto);

		Optional<User> checkUser = userRepository.findByEmail(loginDto.getEmail());

		if (!checkUser.isPresent()) {
			throw new LoginException("This Email id does not exist");
		}

		if (!checkUser.get().isActivate()) {
			throw new LoginException("User account is not activated yet");
		}

		if (!passwordEncoder.matches(loginDto.getPassword(), checkUser.get().getPassword())) {
			throw new LoginException("Password unmatched");
		}

		String jwt = jwtToken.tokenGenerator(loginDto.getEmail());
		return jwt;

	}

	@Override
	public String updateUser(User user) {

		UserUtility.validateEmail(user.getEmail());

		Optional<User> checkUser = userRepository.findByEmail(user.getEmail());
		if (!checkUser.isPresent()) {
			throw new LoginException("Email id does not exist");
		}

		user.setEmail(checkUser.get().getEmail());
		user.setFirstname(checkUser.get().getFirstname());
		user.setLastname(checkUser.get().getLastname());
		user.setPhoneNo(checkUser.get().getPhoneNo());
		user.setPassword(passwordEncoder.encode(checkUser.get().getPassword()));
		userRepository.save(user);

		JwtToken jwtToken=new JwtToken();
		String jwt = jwtToken.tokenGenerator(user.getEmail());
		System.out.println(jwtToken);
		return jwt;
	}

	@Override
	public boolean deleteUser(String email) {

		Optional<User> checkUser = userRepository.findByEmail(email);
		if (checkUser.isPresent()) {
			userRepository.deleteById(email);
			return true;
		}
		return false;
	}

	@Override
	public boolean activateJwt(String token) {

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary("Sarita")).parseClaimsJws(token)
				.getBody();

		Optional<User> user = userRepository.findById(claims.getSubject());
		user.get().setActivate(true);
		userRepository.save(user.get());
		return true;
	}

	@Override
	public void forgetPassword(String email) {

		Optional<User> user = userRepository.findByEmail(email);

		if (!user.isPresent()) {
			throw new LoginException("User is not present");
		}

		String generatedToken = jwtToken.tokenGenerator(email);
		System.out.println(generatedToken);

		MailDTO mail = new MailDTO();
		mail.setTo(email);
		mail.setSubject("Password reset mail");
		mail.setText("http://localhost:8080/user/resetpassword/?token=" + generatedToken);

		producer.sender(mail);
	}

	@Override
	public void passwordReset(String token, PasswordDTO dto) throws Exception {
		
		UserUtility.validateReset(dto);

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary("Sarita")).parseClaimsJws(token)
				.getBody();
		System.out.println("Subject : " + claims.getSubject());

		Optional<User> user = userRepository.findById(claims.getSubject());

		if (!user.isPresent()) {
			throw new Exception("User not found");
		}

		user.get().setPassword(passwordEncoder.encode(dto.getPassword()));
		userRepository.save(user.get());
	}
}
