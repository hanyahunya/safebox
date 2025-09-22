package com.safebox.back.security;

import com.safebox.back.token.MailTokenService;
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
import java.util.Date;

@Component
@RequiredArgsConstructor
public class MailTokenAuthFilter extends OncePerRequestFilter {
    private final MailTokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/report/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = uri.substring("/api/report/".length());
        if (!tokenService.validateToken(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
            return;
        }
        Claims claims = tokenService.getClaims(token);
        String rpiId;
        String parcelId;
        Date expiration;
        try {
            rpiId = claims.get("rpi_id").toString();
            parcelId = claims.get("parcel_id").toString();
            expiration = claims.getExpiration();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token");
            return;
        }
        if (rpiId != null && parcelId != null) {
            StolenPrincipal principal = new StolenPrincipal(rpiId, parcelId, expiration);
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
