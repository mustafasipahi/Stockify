package com.project.envantra.controller;

import com.project.envantra.model.response.InvalidateTokenResponse;
import com.project.envantra.security.dto.AuthenticationRequest;
import com.project.envantra.security.dto.AuthenticationResponse;
import com.project.envantra.service.AuthenticationService;
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
