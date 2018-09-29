package com.bridgelabz.fundonotes.user.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bridgelabz.fundonotes.note.services.Token;
import com.bridgelabz.fundonotes.user.repository.UserRepository;

@Component
public class UserInterceptor implements HandlerInterceptor {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Token jwtToken;
		 
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object object) {
		
		String token=request.getHeader("Authorization");
		if(userRepository.findById(jwtToken.parseJwtToken(token)).isPresent()) {
		request.setAttribute("userId",jwtToken.parseJwtToken(token));
			return true;
		
		}
		return false;
	}
}

