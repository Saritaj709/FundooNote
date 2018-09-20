package com.bridgelabz.fundonotes.user.rabbitmq;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.bridgelabz.fundonotes.user.mail.MailService;
import com.bridgelabz.fundonotes.user.model.MailDTO;
import com.bridgelabz.fundonotes.user.services.AwsConsumerService;

@Service
public class AwsConsumerServiceImpl implements AwsConsumerService {

	/*@Autowired
	private MailService mailService;
	
	@Autowired
	private AmazonSQS sqsClient;

	@Value("${queueUrl}")
	private String queueUrl;
	*/
	@Override
	//@RabbitListener(queues = "${bridgelabz.rabbitmq.queue}")
	public void receive(MailDTO msg) {
		//mailService.sendMail(msg);
		//sqsClient.receiveMessage(msg.toString());
		/*ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
				  .withWaitTimeSeconds(10)
				  .withMaxNumberOfMessages(10);
				 
				List<Message> sqsMessages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
				sqsMessages.get(0).getAttributes();
				sqsMessages.get(0).getBody();
				msg.setSubject("sqs messages");
				msg.setText(sqsMessages.toString()+" ");
				mailService.sendMail(msg);*/
	}
}
