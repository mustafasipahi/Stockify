package com.stockify.project.controller;

import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.service.document.DocumentGetService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentGetService documentGetService;

    @GetMapping(value = "/download/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long documentId) {
        return documentGetService.downloadFile(documentId);
    }

    @GetMapping("/{brokerId}")
    public List<DocumentResponse> getAllDocument(@PathVariable Long brokerId) {
        return documentGetService.getAllDocument(brokerId);
    }
}
