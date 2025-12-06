package com.java.doc_process.controller;

import com.java.doc_process.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/docs")
public class S3Controller {

    private final S3Service s3Service;
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Document Processing Service is up and running!");
    }

    @PostMapping("/createBucket")
    public ResponseEntity<String> createBucket(@RequestParam("bucket") String bucketName) {
        String response = s3Service.createBucket(bucketName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadFile")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bucket") String bucket) throws IOException {
        log.info("Received upload request for file: {} to bucket: {}", file.getOriginalFilename(), bucket);
        String key = file.getOriginalFilename();
        s3Service.upload(bucket,key,file);
        log.info("File uploaded successfully: {} to bucket: {}", key, bucket);
        return ResponseEntity.ok("File uploaded successfully. File Name: " + key).getBody();
    }


}
