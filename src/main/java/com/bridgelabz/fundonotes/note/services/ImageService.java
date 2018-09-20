package com.bridgelabz.fundonotes.note.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.bridgelabz.fundonotes.note.exception.NullValueException;

public interface ImageService {
	String createBucket(String bucketName);

	List<Bucket> showBucket();

	String createFolderInBucket(String bucketName, String folderName);

	String uploadFile(String folderName, MultipartFile file) throws IOException;

	String deleteBucket(String bucketName);

	String deleteFolderAndFile(String bucketName, String folderName);

	File convert(MultipartFile file) throws IOException;

	String uploadFile(String bucketName, String folderName, String file) throws IOException;

	void deleteFile(String url) throws NullValueException;

}
