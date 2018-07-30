package com.bridgelabz.fundonotes.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bridgelabz.fundonotes.note.interceptor.NotesInterceptor;
import com.bridgelabz.fundonotes.user.interceptor.LoggerInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
	
	@Autowired
	LoggerInterceptor loggerInterceptor;
	
	@Autowired
	NotesInterceptor notesInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(loggerInterceptor).addPathPatterns("/**");
		registry.addInterceptor(notesInterceptor).addPathPatterns("/api/notes/**","/api/labels/**");
	}
}
