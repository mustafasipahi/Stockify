package com.stockify.project.security.service;

import com.stockify.project.security.dto.UserTokenInfo;
import com.stockify.project.security.properties.JWTProperties;
import com.stockify.project.security.userdetail.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTTokenService {

    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; //24 h

    private final JWTProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public String findUsernameFromToken(String token) {
        return exportToken(token, Claims::getSubject);
    }

    public Date findExpirationFromToken(String token) {
        return exportToken(token, Claims::getExpiration);
    }

    public String generateToken(UserPrincipal userPrincipal) {
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("stokifySchemaName", userPrincipal.getUserEntity().getStokifySchemaName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsernameFromToken(final String token) {
        return getClaimsByToken(token).getSubject();
    }

    public String extractTokenFromAuthorizationHeader(final String authorizationHeaderValue) {
        if (StringUtils.isEmpty(authorizationHeaderValue)) {
            return "";
        }
        if (!authorizationHeaderValue.startsWith("Bearer ")) {
            return "";
        }
        return authorizationHeaderValue.substring(7);
    }

    public String extractCompanySchemaFromToken(String token) {
        return exportToken(token, claims -> claims.get("stokifySchemaName", String.class));
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return tokenExist(token);
        } catch (SignatureException ex) {
            log.debug("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.debug("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.debug("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.debug("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.debug("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public UserTokenInfo extractUserTokenInfoFromToken(final String token) {
        final Claims claims = getClaimsByToken(token);
        return UserTokenInfo.builder()
                .userId(Long.parseLong(claims.getSubject()))
                .build();
    }

    private <T> T exportToken(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsTFunction.apply(claims);
    }

    private Key getSecretKey() {
        try {
            byte[] key = Decoders.BASE64.decode(jwtProperties.getSecretKey());
            return Keys.hmacShaKeyFor(key);
        } catch (Exception e) {
            log.warn("Secret key is not BASE64 encoded, using direct bytes");
            return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        }
    }

    private boolean tokenExist(final String token) {
        try {
            String memberKey = extractUsernameFromToken(token);
            final String existingToken = redisTemplate.opsForValue().get(memberKey);
            return StringUtils.isNotEmpty(existingToken) && Objects.equals(existingToken, token);
        } catch (Exception e) {
            log.error("Error checking token existence: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
