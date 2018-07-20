package com.bridgelabz.fundonotes.user.services;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${accountActivationLink}")
	private String accountActivationLink;
	
	@Value("${passwordResetLink}")
	private String passwordResetLink;
	
	@Value("${KEY}")
    private String KEY;
    
	@Override
	public List<User> getAllUsers() {

		List<User> userList = userRepository.findAll();
		return userList;
	}

	@Override
	public String saveUser(RegistrationDTO dto) throws RegistrationException {

		UserUtility.validateUser(dto);

		Optional<User> checkUser = userRepository.findById(dto.getId());

		if (checkUser.isPresent()) {
			throw new RegistrationException("User id already exists,unable to register");

		}
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setFirstname(dto.getFirstname());
		user.setLastname(dto.getLastname());
		user.setPhoneNo(dto.getPhoneNo());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		userRepository.insert(user);

		String jwt = jwtToken.tokenGenerator(user.getId());
		
		jwtToken.parseJwtToken(jwt);

		MailDTO mail = new MailDTO();
		mail.setTo(dto.getEmail());
		mail.setSubject("Account activation mail");
		mail.setText("accountActivationLink"+ jwt);
		producer.sender(mail);

		return jwt;
	}

	@Override
	public void getUserById(String id) {

		Optional<User> checkUser = userRepository.findById(id);
		if (!checkUser.isPresent()) {
			throw new LoginException("User is not available");
		}
	}

	@Override
	public String loginUser(LoginDTO loginDto) throws LoginException {

		UserUtility.validateLogin(loginDto);

		Optional<User> checkUser = userRepository.findById(loginDto.getId());

		if (!checkUser.isPresent()) {
			throw new LoginException("This Email id does not exist");
		}

		if (!checkUser.get().isActivate()) {
			throw new LoginException("User account is not activated yet");
		}

		if (!passwordEncoder.matches(loginDto.getPassword(), checkUser.get().getPassword())) {
			throw new LoginException("Password unmatched");
		}

		String jwt = jwtToken.tokenGenerator(loginDto.getId());
		return jwt;

	}

	@Override
	public String updateUser(User user) {

		UserUtility.validateEmail(user.getEmail());

		Optional<User> checkUser = userRepository.findById(user.getId());
		if (!checkUser.isPresent()) {
			throw new LoginException("User id does not exist");
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
	public void deleteUser(String id) {

		Optional<User> checkUser = userRepository.findById(id);
		if (!checkUser.isPresent()) {
			throw new LoginException("User not found");
		}
		userRepository.deleteById(id);
		
	}

	@Override
	public boolean activateJwt(String token) {

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary("KEY")).parseClaimsJws(token)
				.getBody();

		Optional<User> user = userRepository.findById(claims.getSubject());
		user.get().setActivate(true);
		userRepository.save(user.get());
		return true;
	}

	
	@Override
	public boolean activate(String token,String id) {

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary("KEY")).parseClaimsJws(token)
				.getBody();

		Optional<User> user = userRepository.findById(claims.getSubject());
		
		if(!claims.getSubject().equals(id)) {
			throw new LoginException("User not found");
		}
		
		user.get().setActivate(true);
		userRepository.save(user.get());
		return true;
	}

	@Override
	public void forgetPassword(String id,String email) {

		Optional<User> user = userRepository.findById(id);

		if (!user.isPresent()) {
			throw new LoginException("User is not present");
		}

		String generatedToken = jwtToken.tokenGenerator(id);
		System.out.println(generatedToken);

		MailDTO mail = new MailDTO();
		mail.setTo(email);
		mail.setSubject("Password reset mail");
		mail.setText("passwordResetLink" + generatedToken);

		producer.sender(mail);
	}

	@Override
	public void passwordReset(String token, PasswordDTO dto) throws Exception {
		
		UserUtility.validateReset(dto);

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary("Sarita")).parseClaimsJws(token)
				.getBody();
		System.out.println("Subject : " + claims.getSubject());

		Optional<User> user = userRepository.findByEmail(claims.getSubject());

		if (!user.isPresent()) {
			throw new Exception("User not found");
		}

		user.get().setPassword(passwordEncoder.encode(dto.getPassword()));
		userRepository.save(user.get());
	}
}
