package com.bridgelabz.fundonotes.user.mockito;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bridgelabz.fundonotes.user.model.User;
import com.bridgelabz.fundonotes.user.services.UserRepository;
import com.bridgelabz.fundonotes.user.services.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
public class MockitoTest {
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserServiceImpl userServiceImpl;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public boolean getUserByEmail(String email) {

		Optional<User> checkUser = userRepository.findByEmail(email);
		if (checkUser.isPresent()) {
			System.out.println(email + " is available ");
			return true;
		}
		return false;
	}
	@Test
	public List<User> getAllUsers() {

		List<User> userList = userRepository.findAll();
		return userList;
	}
}
