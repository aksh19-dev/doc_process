package com.java.doc_process.modal;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Setter
@Getter
@DynamoDbBean
public class FileMetadata {

    private String fileId;

    private String fileName;
    private String inputS3Key;
    private String outputS3Key;
    private Long size;
    private String uploadedAt;
    private String processedAt;
    private String status;

    @DynamoDbPartitionKey
    public String getFileId() {
        return fileId;
    }

}
