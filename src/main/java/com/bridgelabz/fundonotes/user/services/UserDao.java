package com.bridgelabz.fundonotes.user.services;

import java.io.IOException;
import java.util.Optional;

import org.elasticsearch.client.RestHighLevelClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundonotes.configurations.AwsConfigurations;
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.RestHighLevelClientException;
import com.bridgelabz.fundonotes.note.services.ImageService;
import com.bridgelabz.fundonotes.user.exception.ActivationException;
import com.bridgelabz.fundonotes.user.exception.LoginException;
import com.bridgelabz.fundonotes.user.exception.RegistrationException;
import com.bridgelabz.fundonotes.user.exception.UserNotFoundException;
import com.bridgelabz.fundonotes.user.model.LoginDTO;
import com.bridgelabz.fundonotes.user.model.MailDTO;
import com.bridgelabz.fundonotes.user.model.PasswordDTO;
import com.bridgelabz.fundonotes.user.model.RegistrationDTO;
import com.bridgelabz.fundonotes.user.model.User;
import com.bridgelabz.fundonotes.user.repository.ElasticRepositoryForUser;
import com.bridgelabz.fundonotes.user.repository.HighElasticRepositoryForUser;
import com.bridgelabz.fundonotes.user.repository.RedisRepository;
import com.bridgelabz.fundonotes.user.repository.UserRepository;
import com.bridgelabz.fundonotes.user.utility.UserUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserDao {
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ElasticRepositoryForUser userElasticRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ProducerService producer;

	@Autowired
	private Environment environment;

	@Autowired
	private JwtToken jwtToken;

	@Autowired
	private RedisRepository redisRepository;

	@Autowired
	private ImageService awsS3Service;

	@Autowired
	private AwsProducerService producerService;

	@Autowired
	private AwsConfigurations awsConfigurations;

	@Autowired
	private HighElasticRepositoryForUser highElasticRepoForUser;

	public UserDao(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
	}

	public User registerUser(RegistrationDTO dto) throws RegistrationException, RestHighLevelClientException, IOException {
		
		UserUtility.validateUser(dto);
		
		Optional<User> checkUser = highElasticRepoForUser.findByEmail(dto.getEmail());

		if (checkUser.isPresent()) {
			throw new RegistrationException("User email already exists,unable to register");
		}
		
		User user=null;
		if(!checkUser.isPresent()) {
			
			System.out.println("new User");
		user = modelMapper.map(dto, User.class);
		
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		}
		userRepository.insert(user);
		//userElasticRepository.save(user);
		highElasticRepoForUser.save(user);

		String jwt = jwtToken.tokenGenerator(user.getId());

		jwtToken.parseJwtToken(jwt);

		MailDTO mail = new MailDTO();
		mail.setTo(dto.getEmail());
		mail.setSubject("Account activation mail");
		mail.setText(environment.getProperty("accountActivationLink") + jwt);
		//producer.sender(mail);
		return user;
	}

	public void forgetPassword(String email) throws UserNotFoundException, RestHighLevelClientException, IOException {

		Optional<User> checkUser = highElasticRepoForUser.findByEmail(email);
		//Optional<User> checkUser = highElasticRepo.findById(id);

		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("User is not available");
		}

		String uuid = UserUtility.generateUUId();
		redisRepository.save(uuid, email);

		MailDTO mail = new MailDTO();
		mail.setTo(email);
		mail.setSubject("Password reset mail");
		mail.setText(environment.getProperty("passwordResetLink") + uuid);

		producer.sender(mail);
	}

	public void passwordReset(String uuid, PasswordDTO dto)
			throws UserNotFoundException, RegistrationException, RestHighLevelClientException, IOException, NullValueException {

		UserUtility.validateReset(dto);

		String email = redisRepository.get(uuid);

		// Optional<User> user = userRepository.findById(userId);
		Optional<User> user = highElasticRepoForUser.findByEmail(email);

		if (!user.isPresent()) {
			throw new UserNotFoundException("User not found");
		}

		user.get().setPassword(passwordEncoder.encode(dto.getPassword()));

		userRepository.save(user.get());
		userElasticRepository.save(user.get());

		highElasticRepoForUser.updateById(user.get().getId(), dto);


		redisRepository.delete(uuid);

	}

	public String uploadPic(String id, MultipartFile multipartFile)
			throws UserNotFoundException, IOException, RestHighLevelClientException {

		Optional<User> user = highElasticRepoForUser.findById(id,User.class);

		if (!user.isPresent()) {
			throw new UserNotFoundException(environment.getProperty("UserNotFoundException"));
		}

		User mainUser = user.get();

		// String fileName = mainUser.getId() + environment.getProperty("suffix") +
		// mainUser.getFirstname();

		// awsS3Service.uploadFile(fileName, multipartFile);

		// AmazonS3 client = awsConfigurations.getS3Client();

		// String url = ((AmazonS3Client)
		// client).getResourceUrl(environment.getProperty("bucketName"), fileName);

		String url = "https://s3.amazonaws.com/cgs3bucket/"+id+"/"+mainUser.getFirstname()+"/"+multipartFile.getOriginalFilename();
		mainUser.setProfilePic(url);

		userRepository.save(mainUser);
		//userElasticRepository.save(mainUser);

		highElasticRepoForUser.updateById(mainUser.getId(), mainUser);

		return multipartFile.getOriginalFilename();
	}

	public String removePic(String id) throws UserNotFoundException, IOException, NullValueException, RestHighLevelClientException {

		Optional<User> user = highElasticRepoForUser.findById(id,User.class);

		if (!user.isPresent()) {
			throw new UserNotFoundException(environment.getProperty("UserNotFoundException"));
		}

		User mainUser = user.get();

		//awsS3Service.deleteFile(mainUser.getProfilePic());

		mainUser.setProfilePic(null);

		userRepository.save(mainUser);
	//	userElasticRepository.save(mainUser);
		
		highElasticRepoForUser.save(mainUser);
		return "SUCCESS";
	}

	public String loginUser(LoginDTO loginDto) throws LoginException, UserNotFoundException, ActivationException,
			IOException, RestHighLevelClientException, NullValueException {

		UserUtility.validateLogin(loginDto);

		Optional<User> checkUser = highElasticRepoForUser.findByEmail(loginDto.getEmail());

		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("This Email id does not exist");
		}

	/*	if (!checkUser.get().isActivate()) {
			throw new ActivationException("User account is not activated yet");
		}*/

		if (!passwordEncoder.matches(loginDto.getPassword(), checkUser.get().getPassword())) {
			throw new LoginException("Password unmatched");
		}

		String jwt = jwtToken.tokenGenerator(checkUser.get().getId());
		MailDTO mail = new MailDTO();
		mail.setTo(checkUser.get().getEmail());
		mail.setSubject("Click here to get your token ");
		mail.setText("User token : " + jwt);
		// producerService.send(mail);
		// producerService.sendMessage(mail);
		return jwt;

	}

	public Object getUserById(String id) throws UserNotFoundException, NullValueException, RestHighLevelClientException {

		Optional<User> checkUser = highElasticRepoForUser.findById(id,User.class);

		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("User is not available");
		}
		
		return highElasticRepoForUser.findById(id,User.class);
		
	}
	
	public Object getUserByEmail(String email) throws UserNotFoundException, NullValueException, RestHighLevelClientException, IOException {

		Optional<User> checkUser = highElasticRepoForUser.findByEmail(email);

		if (!checkUser.isPresent()) {
			throw new UserNotFoundException("User is not available");
		}
		
		// highElasticRepo.findByEmail(email);
		return checkUser.get();
	}

}
