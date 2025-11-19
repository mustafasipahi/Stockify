package com.project.envantra.security.service;

import com.project.envantra.model.dto.TokenCacheDto;
import com.project.envantra.security.dto.UserTokenInfo;
import com.project.envantra.security.properties.JWTProperties;
import com.project.envantra.security.userdetail.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.project.envantra.util.AuthenticationUtil.getTokenExpirationDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTTokenService {

    private final JWTProperties jwtProperties;
    private final ConcurrentHashMap<String, TokenCacheDto> tokenCache = new ConcurrentHashMap<>();

    public String findUsernameFromToken(String token) {
        return exportToken(token, Claims::getSubject);
    }

    public Date findExpirationFromToken(String token) {
        return exportToken(token, Claims::getExpiration);
    }

    public String generateToken(UserPrincipal userPrincipal, boolean rememberMe) {
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getUserEntity().getId())
                .setIssuedAt(new Date())
                .setExpiration(getTokenExpirationDate(rememberMe))
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

    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return tokenExist(token);
        } catch (MalformedJwtException ex) {
            log.info("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.info("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.info("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.info("Invalid JWT: {}", ex.getMessage());
        }
        return false;
    }

    public UserTokenInfo extractUserTokenInfoFromToken(final String token) {
        final Claims claims = getClaimsByToken(token);
        return UserTokenInfo.builder()
                .userId(Long.parseLong(claims.getSubject()))
                .build();
    }

    public void storeToken(String username, String token, long expirationTime) {
        cleanExpiredTokens();
        TokenCacheDto entry = new TokenCacheDto(token, System.currentTimeMillis() + expirationTime);
        tokenCache.put(username, entry);
    }

    public void removeToken(String username) {
        tokenCache.remove(username);
    }

    public void cleanExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        tokenCache.entrySet().removeIf(entry -> entry.getValue().expirationTime() < currentTime);
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
            TokenCacheDto cacheEntry = tokenCache.get(memberKey);
            if (cacheEntry == null) {
                return false;
            }
            if (cacheEntry.expirationTime() < System.currentTimeMillis()) {
                tokenCache.remove(memberKey);
                return false;
            }
            return Objects.equals(cacheEntry.token(), token);
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