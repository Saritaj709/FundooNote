package com.bridgelabz.fundonotes.user.mail;

import com.bridgelabz.fundonotes.user.model.MailDTO;

public interface MailService {
	public boolean sendMail(MailDTO mail);	
}
