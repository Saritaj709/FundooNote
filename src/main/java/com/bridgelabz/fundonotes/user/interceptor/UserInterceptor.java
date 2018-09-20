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
	/*@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object object) throws UnAuthorizedException {
		
		String token=request.getHeader("Authorization");
		Optional<User> user=userRepository.findById(jwtToken.parseJwtToken(token));
		if(user.isPresent()) {
			Optional<User> user1=userRepository.findByEmail(user.get().getEmail());
			if(!user1.isPresent()) {
				throw new UnAuthorizedException("the user with given email does not exist");
			}
		request.setAttribute("userEmail",user.get().getEmail());
		request.setAttribute("userId",jwtToken.parseJwtToken(token));
			return true;
		
		}
		return false;
}*/
}

