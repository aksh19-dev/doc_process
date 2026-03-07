package com.java.doc_process.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentProcessor {
    private final SQSService sqsService;
    private final DynamoDBService dynamoDBService;
    private final ObjectMapper mapper;

    @SqsListener("doc-process-queue")
    public void processQueue(String message) {
        log.info("Processing Queue");
        try {
            log.info("Processing Message ID: {}", message);
            processDocument(message);
            log.info("Message processed Successfully: {}", message);
        } catch (Exception e) {
            log.error("Error processing message {}: {}", message, e.getMessage());
        }
    }

    private void processDocument(String messageBody) throws Exception {
        JsonNode jsonNode = mapper.readTree(messageBody);

        String s3Key = jsonNode.get("s3Key").asText();
        String fileId = s3Key;

        dynamoDBService.updateService(fileId, "Processing", null);

        log.info("Processing document: {}", fileId);
        Thread.sleep(10000);

        String outputKey = s3Key.replace("input/", "/output").replace("orignal.pdf", "result.json");

        dynamoDBService.updateService(fileId, "Completed", outputKey);

        log.info("Document processed successfully: {}", outputKey);
    }
}
