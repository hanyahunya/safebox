package com.safebox.back.security;

import com.safebox.back.token.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);

        if (!tokenService.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        try {
            claims = tokenService.getClaims(token);
        } catch (RuntimeException e) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = claims.get("userId", String.class);
        if (userId != null) {
            UserPrincipal userPrincipal = new UserPrincipal(userId);
            Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
