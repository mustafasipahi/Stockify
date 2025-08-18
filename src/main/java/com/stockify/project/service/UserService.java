package com.stockify.project.service;

import com.stockify.project.exception.UnauthenticatedException;
import com.stockify.project.security.dto.UserTokenInfo;
import com.stockify.project.security.service.JWTTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JWTTokenService jwtTokenService;

    public UserTokenInfo getUserTokenInfo(String bearer) {
        String token = jwtTokenService.extractTokenFromAuthorizationHeader(bearer);
        if (StringUtils.isEmpty(token) || !jwtTokenService.validateToken(token)) {
            throw new UnauthenticatedException();
        }
        return jwtTokenService.extractUserTokenInfoFromToken(token);
    }
}
