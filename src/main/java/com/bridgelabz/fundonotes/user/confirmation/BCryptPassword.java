package com.bridgelabz.fundonotes.user.confirmation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BCryptPassword {
	
	@Bean
	public PasswordEncoder encodePassword() {
		return new BCryptPasswordEncoder();
	}
}
