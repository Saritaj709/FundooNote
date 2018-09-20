package com.bridgelabz.fundonotes.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ConfigurationFiles {
	
	@Bean
	public PasswordEncoder encodePassword() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	 
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
}
