package com.java.doc_process.service;


import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Service
public class DynamoDBService {

    private final DynamoDbClient dynamoDbClient;
    public DynamoDBService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveFileMetadata() {
        // Implementation for saving file metadata to DynamoDB


    }

}
