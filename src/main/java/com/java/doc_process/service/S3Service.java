package com.java.doc_process.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String createBucket(String bucketName) {
        try {
            log.info("Checking if bucket exists: {}", bucketName);
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Bucket already exists: {}", bucketName);
            return "Bucket already exists: " + bucketName;
        } catch (S3Exception e) {
            log.info("Bucket Not Found: {}", e.awsErrorDetails().errorMessage() + " (code=" + e.statusCode() + ")");
            if (e.statusCode() == 404) {
                log.info("Creating bucket: {}", bucketName);
                s3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .createBucketConfiguration(CreateBucketConfiguration.builder()
                                .locationConstraint(BucketLocationConstraint.AP_SOUTH_1)
                                        .build()
                        ).build());
                return "Bucket created Successfully: " + bucketName;
            } else if (e.statusCode() == 403) {
                log.warn("Access denied when checking/creating bucket: {}", bucketName);
                throw e;
            } else {
                log.error("Error checking/creating bucket: {}", e.awsErrorDetails().errorMessage());
                throw e;
            }
        } catch (SdkClientException e) {
            log.info("Client error while checking/creating bucket: {}", e.getMessage());
            throw e;
        }
    }

    public void upload(String bucketName, String key, MultipartFile file) throws IOException {
        log.info("Uploading file started: {} to bucket: {}", key, bucketName);
        try {
            validateFile(file);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(),file.getSize()));
            log.info("Response from S3: {}", response);
            log.info("File uploaded successfully: {} to bucket: {}. ETag: {}", key, bucketName, response.eTag());
        } catch (S3Exception e){
            log.error("S3 error during file upload: {}", e.awsErrorDetails().errorMessage());
            throw e;
        } catch (SdkClientException e){
            log.error("Client error during file upload: {}", e.getMessage());
            throw e;
        }
    }

    private void validateFile(MultipartFile file){
        if (file == null || file.isEmpty()){
            throw new IllegalArgumentException("File must not be null or empty");
        }
    }
}
