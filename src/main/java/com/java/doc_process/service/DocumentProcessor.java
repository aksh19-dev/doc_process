package com.java.doc_process.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Scheduled(fixedDelay = 5000)
    public void processQueue(){
        log.info("Processing Queue");
        List<Message> messages = sqsService.receiveMessages();

        for (Message message: messages){
            try {
                log.info("Processing Message ID: {}", message.messageId());
                processDocument(message.body());
                sqsService.deleteMessage(message.receiptHandle());
                log.info("Message deleted Successfully: {}", message.messageId());
            } catch (Exception e) {
                log.error("Error processing message {}: {}", message.messageId(), e.getMessage());
            }
        }
    }

    private void processDocument(String messageBody) throws Exception {
        JsonNode jsonNode = mapper.readTree(messageBody);

        String s3Key = jsonNode.get("s3Key").asText();
        String fileId = s3Key;

        dynamoDBService.updateService(fileId, "Processing", null);

        log.info("Processing document: {}", fileId);
        Thread.sleep(5000);

        String outputKey = s3Key.replace("input/", "/output").replace("orignal.pdf", "result.json");

        dynamoDBService.updateService(fileId, "Completed", outputKey);

        log.info("Document processed successfully: {}", outputKey);
    }
}
