package com.java.doc_process.service;


import jakarta.annotation.PostConstruct;
import com.java.doc_process.modal.FileMetadata;
import com.java.doc_process.repository.FileMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;

@Slf4j
@Service
public class DynamoDBService {

    private final DynamoDbClient dynamoDbClient;
    private final FileMetadataRepository repository;

    @Value("${aws.dynamo-db.table-name}")
    private String tableName;

    public DynamoDBService(DynamoDbClient dynamoDbClient, FileMetadataRepository repository) {
        this.dynamoDbClient = dynamoDbClient;
        this.repository = repository;
    }

    public void saveFileMetadata(String bucketName, String key, long size,String contentType,String eTag) {
        try {
            FileMetadata metadata = new FileMetadata();
            metadata.setFileId(key);
            metadata.setFileName(key);
            metadata.setInputS3Key(key);
            metadata.setSize(size);
            metadata.setStatus("Pending");
            metadata.setUploadedAt(Instant.now().toString());
            repository.save(metadata);
            log.info("File metadata saved in DynamoDB: {}", tableName);
        } catch (Exception e) {
            log.error("Exception occurred while posting metadata to DB " + e.getMessage());
        }
    }

    public void updateService(String fileId, String status, String outputKey){
        FileMetadata metadata = repository.findById(fileId);
        if (metadata != null){
            metadata.setStatus(status);
            if (outputKey != null) metadata.setOutputS3Key(outputKey);
            metadata.setProcessedAt(Instant.now().toString());
            repository.save(metadata);
        }
    }

}
