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
    private final DynamoDBService dynamoDBService;

    public S3Service(S3Client s3Client, DynamoDBService dynamoDBService) {
        this.s3Client = s3Client;
        this.dynamoDBService = dynamoDBService;
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

            try {
                dynamoDBService.saveFileMetadata(bucketName,key,file.getSize(),file.getContentType(),response.eTag());
            } catch (Exception e) {
                log.error("Error saving file metadata to DynamoDB for file: {} in bucket: {}: {}", key, bucketName, e.getMessage());
            }
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

//    public String getFileMetadata(String bucketName, String key) {
//        log.info("Fetching metadata for file: {} in bucket: {}", key, bucketName);
//        try {
//            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(key)
//                    .build();
//            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
//            StringBuilder metadataInfo = new StringBuilder();
//            metadataInfo.append("File Metadata for ").append(key).append(" in bucket ").append(bucketName).append(":\n");
//            metadataInfo.append("Content Type: ").append(headObjectResponse.contentType()).append("\n");
//            metadataInfo.append("Content Length: ").append(headObjectResponse.contentLength()).append("\n");
//            metadataInfo.append("ETag: ").append(headObjectResponse.eTag()).append("\n");
//            metadataInfo.append("Last Modified: ").append(headObjectResponse.lastModified()).append("\n");
//            log.info("Metadata fetched successfully for file: {} in bucket: {}", key, bucketName);
//            return metadataInfo.toString();
//        } catch (NoSuchKeyException e) {
//            log.error("File not found: {} in bucket: {}", key, bucketName);
//            return "File not found: " + key + " in bucket: " + bucketName;
//        } catch (S3Exception e) {
//            log.error("S3 error while fetching metadata for file: {} in bucket: {}: {}", key, bucketName, e.awsErrorDetails().errorMessage());
//            throw e;
//        } catch (SdkClientException e) {
//            log.error("Client error while fetching metadata for file: {} in bucket: {}: {}", key, bucketName, e.getMessage());
//            throw e;
//        }
//    }
}
