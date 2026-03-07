package com.java.doc_process.service;


import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.java.doc_process.modal.FileMetadata;
import com.java.doc_process.repository.FileMetadataRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DynamoDBService {

    private final FileMetadataRepository repository;

    @Value("${aws.dynamo-db.table-name}")
    private String tableName;

    public DynamoDBService(FileMetadataRepository repository) {
        this.repository = repository;
    }

    public void saveFileMetadata(String bucketName, String key, long size,String contentType,String eTag) {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileId(key);
        metadata.setFileName(key);
        metadata.setInputS3Key(key);
        metadata.setSize(size);
        metadata.setStatus("Pending");
        metadata.setUploadedAt(Instant.now().toString());
        repository.save(metadata);
        log.info("File metadata saved in DynamoDB: {}", tableName);
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
