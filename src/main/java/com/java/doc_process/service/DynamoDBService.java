package com.java.doc_process.service;


import com.java.doc_process.repository.FileMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DynamoDBService {

    private final DynamoDbClient dynamoDbClient;
    private final FileMetadataRepository repository;

    public DynamoDBService(DynamoDbClient dynamoDbClient, FileMetadataRepository repository, FileMetadataRepository repository1) {
        this.dynamoDbClient = dynamoDbClient;
        this.repository = repository;
    }

    public void saveFileMetadata(String bucketName, String key, long size,String contentType,String eTag) {
        // Implementation for saving file metadata
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalArgumentException("bucketName must not be null or empty");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be null or empty");
        }

        Map<String, AttributeValue> item = new HashMap<>();
        String id = bucketName + "/" + key;
        item.put("fileId", AttributeValue.builder().s(id).build());
        item.put("bucketName", AttributeValue.builder().s(bucketName).build());
        item.put("fileKey", AttributeValue.builder().s(key).build());
        item.put("size", AttributeValue.builder().n(Long.toString(size)).build());
        if (contentType != null){
            item.put("contentType", AttributeValue.builder().s(contentType).build());
        }
        if (eTag != null){
            item.put("eTag", AttributeValue.builder().s(eTag).build());
        }
        item.put("UploadTimestamp", AttributeValue.builder().s(Instant.now().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("FileMetadata")
                .item(item)
                .build();
        try {
            dynamoDbClient.putItem(request);
            log.info("File metadata saved successfully for key: {}", key);
        } catch (Exception e) {
           log.error("Exception occurred while posting metadata to DB " + e.getMessage());
        }
    }

}
