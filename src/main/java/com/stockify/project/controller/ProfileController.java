package com.stockify.project.controller;

import com.stockify.project.model.dto.ProfileDto;
import com.stockify.project.model.request.ProfileUpdateRequest;
import com.stockify.project.service.ProfileGetService;
import com.stockify.project.service.ProfilePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfilePostService profilePostService;
    private final ProfileGetService profileGetService;

    @PutMapping("/update")
    public void update(@RequestBody ProfileUpdateRequest request) {
        profilePostService.update(request);
    }

    @GetMapping("/detail")
    public ProfileDto detail() {
        return profileGetService.detail();
    }

    @PostMapping(value = "/upload/profile-image", consumes = MULTIPART_FORM_DATA_VALUE)
    public void uploadProfileImage(@RequestPart(name = "file") MultipartFile file) {
        profilePostService.uploadProfileImage(file);
    }

    @PostMapping(value = "/upload/company-logo", consumes = MULTIPART_FORM_DATA_VALUE)
    public void uploadCompanyLogo(@RequestPart(name = "file") MultipartFile file) {
        profilePostService.uploadCompanyLogo(file);
    }
}
