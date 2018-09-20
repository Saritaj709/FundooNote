package com.bridgelabz.fundonotes.user.services;

import com.bridgelabz.fundonotes.user.model.MailDTO;

public interface AwsConsumerService {
public void receive(MailDTO dto);
}
