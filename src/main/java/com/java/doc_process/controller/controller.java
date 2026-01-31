package com.java.doc_process.controller;

import com.java.doc_process.modal.FileMetadata;
import com.java.doc_process.repository.FileMetadataRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/example")
public class controller {

    private final FileMetadataRepository repository;

    public controller(FileMetadataRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/save-metadata")
    public ResponseEntity createFile(@RequestBody FileMetadata fileMetadata){
        repository.save(fileMetadata);
        return ResponseEntity.status(HttpStatus.CREATED).body("File metadata saved successfully");
    }

    @GetMapping("/{id}")
    public FileMetadata getFile(@PathVariable("id") String id) {
        return repository.findById(id);
    }
}
