package com.bridgelabz.fundonotes.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class SQSConfigurations {

	@Autowired
	private Environment environment;
	
	@Autowired
	private AwsConfigurations awsConfigurations;
	
	/*@Bean
	public AmazonSQS sqsClient() {
		AWSCredentials credentials = new BasicAWSCredentials(environment.getProperty("aws_access_key_id"),
				environment.getProperty("aws_secret_access_key"));
		AmazonSQS sqs = AmazonSQSClientBuilder.standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_EAST_2)
				  .build();
		return sqs;
	}

	@Bean
	public SQSConnectionFactory sqsConnectionFactory() {
	    return new SQSConnectionFactory(new ProviderConfiguration(),
	            AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_2).withCredentials(new AWSCredentialsProvider() {

	            	@Override
	                public void refresh() {

	                }

	            	@Override
	                public AWSCredentials getCredentials() {

	                    return awsConfigurations.getAwsCredentials();
	                }
	            }));

	}*/
}
