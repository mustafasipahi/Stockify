package com.stockify.project.security.config;

import com.stockify.project.security.service.JWTTokenService;
import com.stockify.project.security.userdetail.UserPrincipal;
import com.stockify.project.util.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTTokenFilterConfig extends OncePerRequestFilter {

    private final JWTTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) {

        final String header = request.getHeader("Authorization");
        try {
            if (header != null && header.startsWith("Bearer ")) {
                final String token = jwtTokenService.extractTokenFromAuthorizationHeader(header);
                if (jwtTokenService.validateToken(token)) {
                    final String username = jwtTokenService.findUsernameFromToken(token);
                    final Date expirationDate = jwtTokenService.findExpirationFromToken(token);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UserPrincipal principal = (UserPrincipal) userDetails;
                        TenantContext.setCurrentTenantId(principal.getUserEntity());
                        if (username.equals(userDetails.getUsername()) && expirationDate.after(new Date())) {
                            final UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT authentication error: {}", e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }
}
