package com.bridgelabz.fundonotes.user.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.bridgelabz.fundonotes.user.model.MailDTO;
import com.bridgelabz.fundonotes.user.services.AwsProducerService;
import com.google.gson.Gson;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

@Service
public class AwsProducerServiceImpl implements AwsProducerService {
/*
	@Autowired
	private AmazonSQS sqsClient;

	@Value("${queueUrl}")
	private String sqsUrl;

	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Value("${queueName}")
	private String queueName;
*/
	@Override
	public void send(MailDTO msg) {

	/*	Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
		messageAttributes.put("AttributeOne",
				new MessageAttributeValue().withStringValue("This is an attribute").withDataType("String"));

		SendMessageRequest sendMessageStandardQueue = new SendMessageRequest().withQueueUrl(sqsUrl)
				.withMessageBody(msg.getText()).withDelaySeconds(30).withMessageAttributes(messageAttributes);

		sqsClient.sendMessage(sendMessageStandardQueue);*/
	}

	@Override
	public void sendMessage(MailDTO mail) {

	/*	Gson gson = new Gson();
		String message = gson.toJson(mail);
		jmsTemplate.send(queueName, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});*/
	}
}
