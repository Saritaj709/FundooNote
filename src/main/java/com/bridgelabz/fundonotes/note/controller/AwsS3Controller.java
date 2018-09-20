package com.bridgelabz.fundonotes.note.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.bridgelabz.fundonotes.note.services.ImageService;

@RestController
@RequestMapping("/s3")
public class AwsS3Controller {

	@Autowired
	private ImageService awsS3Service;

	public static final Logger logger = LoggerFactory.getLogger(AwsS3Controller.class);

	// -------------Get All Buckets--------------------------

	@RequestMapping(value = "/buckets", method = RequestMethod.GET)
	public List<Bucket> showBuckets() {
		return awsS3Service.showBucket();
	}

	@RequestMapping(value = "/create-bucket", method = RequestMethod.POST)
	public String createBucket(@RequestParam(value = "/bucketName") String bucketName) {
		return awsS3Service.createBucket(bucketName);
	}

	@RequestMapping(value = "/createfolder-in-bucket", method = RequestMethod.POST)
	public String createFolderInBucket(@RequestParam(value = "/bucketName") String bucketName,
			@RequestParam(value = "/folderName") String folderName) {
		return awsS3Service.createFolderInBucket(bucketName, folderName);
	}

	@RequestMapping(value = "/upload-file", method = RequestMethod.POST)
	public String uploadFile(@RequestParam(value = "/bucketName") String bucketName,
			@RequestParam(value = "/folderName") String folderName, @RequestParam(value = "/fileName") MultipartFile file) throws IOException {
		return awsS3Service.uploadFile(folderName, file);
	}

	@RequestMapping(value = "/deletebucket", method = RequestMethod.DELETE)
	public String deleteBucket(@RequestParam(value = "/bucketName") String bucketName) {
		return awsS3Service.deleteBucket(bucketName);
	}

	@RequestMapping(value = "deletefolder-file", method = RequestMethod.DELETE)
	public String deleteFolderAndFile(@RequestParam(value = "/bucketName") String bucketName,
			@RequestParam(value = "/folderName") String folderName) {
		return awsS3Service.deleteFolderAndFile(bucketName, folderName);
	}
	
	@RequestMapping(value="multipart-file",method=RequestMethod.POST)
	public ResponseEntity<File> multipartFile(@RequestParam(value="file")MultipartFile multipartFile) throws IOException {
		awsS3Service.convert(multipartFile);
		return new ResponseEntity<>(awsS3Service.convert(multipartFile),HttpStatus.OK);
	}

}

