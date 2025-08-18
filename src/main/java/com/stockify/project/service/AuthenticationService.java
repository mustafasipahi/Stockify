package com.stockify.project.service;

import com.stockify.project.security.dto.AuthenticationRequest;
import com.stockify.project.security.dto.AuthenticationResponse;
import com.stockify.project.security.service.JWTTokenService;
import com.stockify.project.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenService jwtTokenService;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            authenticationRequest.getUsername(),
            authenticationRequest.getPassword()
        );

        final Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        final UserPrincipal userPrincipal = (UserPrincipal) authenticate.getPrincipal();

        String token = jwtTokenService.generateToken(userPrincipal);
        redisTemplate.opsForValue().set(authenticationRequest.getUsername(), token);

        return AuthenticationResponse.builder()
            .token(token)
            .build();
    }
}
