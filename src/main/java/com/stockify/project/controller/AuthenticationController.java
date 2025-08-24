package com.stockify.project.controller;

import com.stockify.project.model.response.InvalidateTokenResponse;
import com.stockify.project.security.dto.AuthenticationRequest;
import com.stockify.project.security.dto.AuthenticationResponse;
import com.stockify.project.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
        return authenticationService.login(authenticationRequest);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<InvalidateTokenResponse> logout(@RequestHeader(value = AUTHORIZATION) final String authorization) {
        return ResponseEntity.ok(authenticationService.logout(authorization));
    }
}
