package com.stockify.project.controller;

import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.security.userdetail.UserPrincipal;
import com.stockify.project.service.DocumentGetService;
import com.stockify.project.service.DocumentPostService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public DocumentResponse uploadFile(@AuthenticationPrincipal final UserPrincipal userPrincipal,
                                       @RequestParam MultipartFile file, DocumentUploadRequest request) {
        String username = userPrincipal.getUserEntity().getUsername();
        return documentPostService.uploadFile(file, request, username);
    }

    @GetMapping(value = "/download/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long documentId) {
        return documentGetService.downloadFile(documentId);
    }

    @GetMapping("/{brokerId}")
    public List<DocumentResponse> getAllDocument(@PathVariable Long brokerId) {
        return documentGetService.getAllDocument(brokerId);
    }
}
