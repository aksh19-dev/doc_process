package com.java.doc_process.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Slf4j
@Service
public class SQSService {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Value("${aws.sqs.queue-name}")
    private String queueName;

    public SQSService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void publishMessage(String messageBody) {
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
        log.info("Message published to SQS: {}", messageBody);
    }

    public List<Message> receiveMessages() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5) // Batch size
                .waitTimeSeconds(5)    // Long polling
                .build();
        return sqsClient.receiveMessage(receiveRequest).messages();
    }

    public void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl).
                receiptHandle(receiptHandle)
                .build());
    }
}