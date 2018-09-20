package com.bridgelabz.fundonotes.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfigurations {

	@Autowired
	private Environment environment;

	@Bean
	public AWSCredentials getAwsCredentials() {
		AWSCredentials credentials = null;
		try {
			credentials = new AWSCredentials() {
				@Override
				public String getAWSSecretKey() {
					return environment.getProperty("aws_secret_access_key");
				}

				@Override
				public String getAWSAccessKeyId() {
					return environment.getProperty("aws_access_key_id");
				}
			};
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return credentials;
	}

	@Bean
	public AmazonS3 getS3Client() {

		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(getAwsCredentials())).withRegion(Regions.US_EAST_1)
				.build();
		return s3client;
	}
}
