package com.stockify.project.controller;

import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.service.document.DocumentGetService;
import com.stockify.project.service.document.DocumentPostService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentPostService documentPostService;
    private final DocumentGetService documentGetService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public DocumentResponse uploadFile(@RequestParam MultipartFile file, DocumentUploadRequest request) {
        return documentPostService.uploadRestFile(file, request);
    }

    @GetMapping(value = "/download/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long documentId) {
        return documentGetService.downloadFile(documentId);
    }

    @GetMapping(value = "/download/out/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadOutFile(@PathVariable Long documentId) {
        return documentGetService.downloadOutFile(documentId);
    }

    @GetMapping("/{brokerId}")
    public List<DocumentResponse> getAllDocument(@PathVariable Long brokerId) {
        return documentGetService.getAllDocument(brokerId);
    }
}
