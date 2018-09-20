package com.bridgelabz.fundonotes.note.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.bridgelabz.fundonotes.configurations.AwsConfigurations;
import com.bridgelabz.fundonotes.note.exception.NullValueException;
import com.bridgelabz.fundonotes.note.exception.S3ClientException;

@Service
public class AwsS3ServiceImpl implements ImageService {

	@Autowired
	private Environment environment;

	@Value("${suffix}")
	private String SUFFIX;

	@Autowired
	private AwsConfigurations client;

	@Override
	public String createBucket(String bucketName) {

		AmazonS3 s3Client = client.getS3Client();
		s3Client.createBucket(bucketName);
		return bucketName;
	}

	@Override
	public List<Bucket> showBucket() {

		AmazonS3 s3Client = client.getS3Client();

		for (Bucket bucket : s3Client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		return s3Client.listBuckets();
	}

	@Override
	public String uploadFile(String folderName, MultipartFile multipartFile) throws IOException {

		String folder = folderName + SUFFIX + multipartFile.getOriginalFilename();

		System.out.println("folder " + folder);

		File fileName = convert(multipartFile);

		try {

			AmazonS3 s3Client = client.getS3Client();

			s3Client.putObject(environment.getProperty("bucketName"), folder, fileName);
		} catch (Exception e) {
			throw new S3ClientException(environment.getProperty("S3ClientException"));
		}

		return multipartFile.getOriginalFilename();
	}

	@Override
	public String uploadFile(String bucketName, String fileName, String file) throws IOException {
		AmazonS3 s3Client = client.getS3Client();

		s3Client.putObject(
				new PutObjectRequest(bucketName, fileName, new File(environment.getProperty("sourceFolder") + file))
						.withCannedAcl(CannedAccessControlList.PublicRead));
		return fileName;
	}

	@Override
	public String deleteBucket(String bucketName) {
		AmazonS3 s3Client = client.getS3Client();

		s3Client.deleteBucket(bucketName);
		return bucketName;
	}

	@Override
	public String createFolderInBucket(String bucketName, String folderName) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, emptyContent,
				metadata);
		AmazonS3 s3Client = client.getS3Client();

		s3Client.putObject(putObjectRequest);
		return folderName;
	}

	/**
	 * This method first deletes all the files in given folder and than the folder
	 * itself
	 */
	@Override
	public String deleteFolderAndFile(String bucketName, String folderName) {

		AmazonS3 s3Client = client.getS3Client();

		List<S3ObjectSummary> fileList = s3Client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			s3Client.deleteObject(bucketName, file.getKey());
		}
		s3Client.deleteObject(bucketName, folderName);
		return folderName;
	}

	@Override
	public void deleteFile(String url) throws NullValueException {

		if (url != null) {
			String[] files = url.split(environment.getProperty("imageLink"));
			System.out.println(files[1]);
			AmazonS3 s3Client = client.getS3Client();

			s3Client.deleteObject(environment.getProperty("bucketName"), files[1]);
		} else {
			throw new NullValueException("No such image available");
		}
	}

	@Override
	public File convert(MultipartFile file) throws IOException {
		try {

			File convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			System.out.println(file.getOriginalFilename());
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			return convFile;
		} catch (Exception e) {

			e.printStackTrace(System.err);
			throw new RuntimeException();
		}
	}
}
