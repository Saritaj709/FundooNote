package com.bridgelabz.fundonotes.user.services;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.user.exception.ActivationException;
import com.bridgelabz.fundonotes.user.exception.LoginException;
import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.user.mail.MailService;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.MailDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;
import com.bridgelabz.fundonotes.user.model.User;
import com.bridgelabz.fundonotes.user.repository.UserRepository;
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
	
	@Value("${Key}")
    private String Key;
	
	/*@Autowired
	private MailService mailService;*/
    
	@Override
	public List<User> getAllUsers() {

		List<User> userList = userRepository.findAll();
		return userList;
	}

	@Override
	public String registerUser(RegistrationDTO dto) throws RegistrationException {

		UserUtility.validateUser(dto);

		Optional<User> checkUser = userRepository.findByEmail(dto.getEmail());

		if (checkUser.isPresent()) {
			throw new RegistrationException("User email already exists,unable to register");

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
		mail.setText(accountActivationLink  + jwt);
		//mail.setText("Click here to verify your account:\\n\\n\" + \"http://192.168.0.73:8080/user/activateaccount/?token="+ jwt);
		producer.sender(mail);
        //mailService.sendMail(mail);
		return jwt;
	}

	@Override
	public void getUserById(String id) throws UserNotFoundException {

		Optional<User> checkUser = userRepository.findById(id);
		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("User is not available");
		}
	}

	@Override
	public String loginUser(LoginDTO loginDto) throws LoginException, UserNotFoundException, ActivationException {

		UserUtility.validateLogin(loginDto);

		Optional<User> checkUser = userRepository.findByEmail(loginDto.getEmail());

		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("This Email id does not exist");
		}

		if (!checkUser.get().isActivate()) {
			throw new ActivationException("User account is not activated yet");
		}

		if (!passwordEncoder.matches(loginDto.getPassword(), checkUser.get().getPassword())) {
			throw new LoginException("Password unmatched");
		}

		String jwt = jwtToken.tokenGenerator(loginDto.getId());
		return jwt;

	}

	@Override
	public String updateUser(User user) throws UserNotFoundException {

		UserUtility.validateEmail(user.getEmail());

		Optional<User> checkUser = userRepository.findById(user.getId());
		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("User id does not exist");
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
	public void deleteUser(String email) throws UserNotFoundException {

		Optional<User> checkUser = userRepository.findByEmail(email);
		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("User not found");
		}
		userRepository.deleteByEmail(email);
		
	}

	@Override
	public boolean activateJwt(String token) {

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(Key)).parseClaimsJws(token)
				.getBody();

		Optional<User> user = userRepository.findById(claims.getSubject());
		user.get().setActivate(true);
		userRepository.save(user.get());
		return true;
	}

	
	@Override
	public boolean activate(String token) throws UserNotFoundException {
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(Key)).parseClaimsJws(token)
				.getBody();

		Optional<User> user = userRepository.findById(claims.getSubject());
		
		if(!claims.getSubject().equals(user.get().getId())) {
	 	throw new UserNotFoundException("User not found");
		}
		
		user.get().setActivate(true);
		userRepository.save(user.get());
		return true;
	}

	@Override
	public void forgetPassword(String id) throws UserNotFoundException {

		Optional<User> user = userRepository.findById(id);

		if (!user.isPresent()) {
			throw new UserNotFoundException("User is not present");
		}

		String generatedToken = jwtToken.tokenGenerator(id);
		System.out.println(generatedToken);

		MailDTO mail = new MailDTO();
		mail.setTo(user.get().getEmail());
		mail.setSubject("Password reset mail");
		mail.setText(passwordResetLink  + generatedToken);

		producer.sender(mail);
	}

	@Override
	public void passwordReset(String token, PasswordDTO dto) throws UserNotFoundException, RegistrationException {
		
		UserUtility.validateReset(dto);
		
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(Key)).parseClaimsJws(token)
				.getBody();
		
		System.out.println("Subject : " + claims.getSubject());

		Optional<User> user = userRepository.findById(claims.getSubject());

		if (!user.isPresent()) {
			throw new UserNotFoundException("User not found");
		}

		user.get().setPassword(passwordEncoder.encode(dto.getPassword()));
		userRepository.save(user.get());
	}
}
