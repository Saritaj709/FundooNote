package com.bridgelabz.fundonotes.user.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bridgelabz.fundonotes.user.mail.MailService;
import com.bridgelabz.fundonotes.user.model.MailDTO;
import com.google.gson.Gson;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class SQSListener {

//public class SQSListener implements MessageListener {

	/*@Autowired
	private MailService mailService;

	private static final Logger LOGGER = LoggerFactory.getLogger(SQSListener.class);

	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			LOGGER.info("Received message " + textMessage.getText());
			Gson gson = new Gson();
			MailDTO msg = gson.fromJson(textMessage.getText(), MailDTO.class);
			mailService.sendMail(msg);

		} catch (JMSException e1) {
			e1.printStackTrace();
		}
	}*/
}
