package com.bridgelabz.fundonotes.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.bridgelabz.fundonotes.user.rabbitmq.SQSListener;

@Configuration
public class JMSSQSConfig {

	/*@Value("${queueName}")
	private String queueName;

	@Autowired
	private SQSListener sqsListener;

	@Autowired
	private SQSConfigurations sqsConfig;

	@Bean
	public DefaultMessageListenerContainer jmsListenerContainer() {

		DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
		dmlc.setConnectionFactory(sqsConfig.sqsConnectionFactory());
		dmlc.setDestinationName(queueName);
		dmlc.setMessageListener(sqsListener);
		return dmlc;

	}

	@Bean
	public JmsTemplate createJMSTemplate() {

		JmsTemplate jmsTemplate = new JmsTemplate(sqsConfig.sqsConnectionFactory());
		jmsTemplate.setDefaultDestinationName(queueName);
		jmsTemplate.setDeliveryPersistent(false);
		return jmsTemplate;
	}*/
}
