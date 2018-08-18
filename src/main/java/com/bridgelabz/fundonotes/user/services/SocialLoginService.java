package com.bridgelabz.fundonotes.user.services;

import com.bridgelabz.fundonotes.user.exception.RegistrationException;

public interface SocialLoginService {

	public String createFacebookAuthorizationURL();
	public void createFacebookAccessToken(String code);
	public Object getName() throws RegistrationException;

}
