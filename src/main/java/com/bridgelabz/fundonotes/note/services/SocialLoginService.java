package com.bridgelabz.fundonotes.note.services;

	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.social.facebook.api.Facebook;
	import org.springframework.social.facebook.api.impl.FacebookTemplate;
	import org.springframework.social.facebook.connect.FacebookConnectionFactory;
	import org.springframework.social.oauth2.AccessGrant;
	import org.springframework.social.oauth2.OAuth2Operations;
	import org.springframework.social.oauth2.OAuth2Parameters;
	import org.springframework.stereotype.Service;

	@Service
	public class SocialLoginService {

		@Value("${spring.social.facebook.appId}")
		String facebookAppId;
		@Value("${spring.social.facebook.appSecret}")
		String facebookSecret;
		
		String accessToken;

		public String createFacebookAuthorizationURL() {
			FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
			OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
			OAuth2Parameters params = new OAuth2Parameters();
			params.setRedirectUri("http://localhost:8081/swagger-ui.html#!/facebook-login-controller/createFacebookAuthorizationUsingGET");
			params.setScope("public_profile,email,user_birthday");
			return oauthOperations.buildAuthorizeUrl(params);
		}
		
		public void createFacebookAccessToken(String code) {
		    FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		    AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, "http://localhost:8081/swagger-ui.html#!/facebook-login-controller/createFacebookAuthorizationUsingGET", null);
		    accessToken = accessGrant.getAccessToken();
		}

		public String getName() {
		    Facebook facebook = new FacebookTemplate(accessToken);
		  //  String[] fields = {"id", "name"};
		    return facebook.fetchObject("me",String.class,"name");
		}
	}