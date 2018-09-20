package com.bridgelabz.fundonotes.user.services;

import com.bridgelabz.fundonotes.user.model.MailDTO;

public interface AwsProducerService {
public void send(MailDTO dto);

void sendMessage(MailDTO mail);
}
