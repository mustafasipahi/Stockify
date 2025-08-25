package com.stockify.project.service;

import com.stockify.project.exception.AuthenticationException;
import com.stockify.project.model.response.InvalidateTokenResponse;
import com.stockify.project.security.dto.AuthenticationRequest;
import com.stockify.project.security.dto.AuthenticationResponse;
import com.stockify.project.security.service.JWTTokenService;
import com.stockify.project.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenService jwtTokenService;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        try {
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword());
            final Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            final UserPrincipal userPrincipal = (UserPrincipal) authenticate.getPrincipal();
            setTenant(userPrincipal.getUserEntity().getId());
            String token = jwtTokenService.generateToken(userPrincipal);
            redisTemplate.opsForValue().set(authenticationRequest.getUsername(), token);
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    public InvalidateTokenResponse logout(String authorization) {
        try {
            String token = jwtTokenService.extractTokenFromAuthorizationHeader(authorization);
            if (StringUtils.isEmpty(token)) {
                return InvalidateTokenResponse.builder()
                        .success(false)
                        .message("Token not found in authorization header")
                        .build();
            }
            String username = jwtTokenService.extractUsernameFromToken(token);
            if (StringUtils.isEmpty(username)) {
                return InvalidateTokenResponse.builder()
                        .success(false)
                        .message("Username not found in token")
                        .build();
            }
            redisTemplate.delete(username);
            log.info("User {} logged out successfully", username);
            return InvalidateTokenResponse.builder()
                    .success(true)
                    .message("Logout successful")
                    .build();
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            return InvalidateTokenResponse.builder()
                    .success(false)
                    .message("Logout failed")
                    .build();
        }
    }

    private void setTenant(Long userId) {

    }
}
