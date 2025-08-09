package com.safebox.back.token;

import com.safebox.back.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final String issuer;
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet(); // 로그아웃된 토큰 저장

    public JwtTokenServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMs, // 기본값: 24시간
            @Value("${jwt.issuer:safebox}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
        this.issuer = issuer;
    }

    /**
     * JWT 토큰 생성
     */
    @Override
    public String generateToken(String userId, Role role) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationMs);

            String token = Jwts.builder()
                    .setIssuer(issuer)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .claim("user_id", userId)
                    .claim("role", role.name())
                    .claim("tokenType", "ACCESS_TOKEN")
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

            log.info("JWT 토큰 생성 완료 - 사용자 ID: {}", userId);
            return token;
        } catch (Exception e) {
            log.error("JWT 토큰 생성 중 오류 발생 - 사용자 ID: {}", userId, e);
            throw new JwtTokenException("토큰 생성 실패", e);
        }
    }

    /**
     * 토큰 검증
     */
    @Override
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.debug("토큰이 null이거나 비어있음");
            return false;
        }

        try {
            // 블랙리스트 확인
            if (blacklistedTokens.contains(token)) {
                log.debug("블랙리스트된 토큰 접근 시도");
                return false;
            }

            // JWT 파싱 및 검증
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.debug("잘못된 형식의 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.debug("JWT 서명 검증 실패: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰 인수 오류: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 예상치 못한 오류 발생", e);
            return false;
        }
    }

    /**
     * 토큰에서 로그인 ID 추출
     */
    @Override
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = extractClaimsWithoutValidation(token);
            if (claims == null) {
                return null;
            }
            return claims.get("user_id", String.class);
        } catch (Exception e) {
            log.error("JWT 토큰에서 사용자 ID 추출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 토큰에서 권한 추출
     */
    @Override
    public String getRoleFromToken(String token) {
        try {
            Claims claims = extractClaimsWithoutValidation(token);
            if (claims == null) {
                return null;
            }
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("JWT 토큰에서 권한추출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 로그아웃 처리 (블랙리스트에 토큰 저장)
     */
    @Override
    public void invalidateToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
            String loginId = getUserIdFromToken(token);
            log.info("JWT 토큰 무효화 완료 - 사용자 ID: {}", loginId);
        }
    }

    /**
     * 토큰 갱신
     */
    @Override
    public String refreshToken(String token) {
        if (!validateToken(token)) {
            throw new JwtTokenException("유효하지 않은 토큰입니다.");
        }

        String loginId = getUserIdFromToken(token);
        if (loginId == null) {
            throw new JwtTokenException("토큰에서 사용자 정보를 찾을 수 없습니다.");
        }

        // 기존 토큰 무효화
        invalidateToken(token);

        // 새 토큰 생성 및 반환
        return generateToken(loginId, Role.USER);
    }

    /**
     * 토큰 만료 시간 조회
     */
    @Override
    public Date getExpirationDate(String token) {
        try {
            Claims claims = extractClaimsWithoutValidation(token);
            if (claims == null) {
                return null;
            }
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("JWT 토큰에서 만료 시간 추출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 토큰의 남은 유효 시간 조회 (밀리초)
     */
    @Override
    public long getRemainingValidityTime(String token) {
        Date expirationDate = getExpirationDate(token);
        if (expirationDate == null) {
            return 0;
        }

        long now = System.currentTimeMillis();
        long expiry = expirationDate.getTime();

        return Math.max(0, expiry - now);
    }

    /**
     * JWT 토큰에서 Claims 추출 (검증 포함)
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT 토큰에서 Claims 추출 (검증 없이)
     */
    private Claims extractClaimsWithoutValidation(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 블랙리스트 정리 (스케줄러로 주기적 실행 권장)
     */
    public void cleanupBlacklist() {
        // 만료된 토큰들을 블랙리스트에서 제거
        blacklistedTokens.removeIf(token -> {
            try {
                Date expiration = getExpirationDate(token);
                return expiration != null && expiration.before(new Date());
            } catch (Exception e) {
                // 파싱할 수 없는 토큰은 제거
                return true;
            }
        });
        log.info("블랙리스트 정리 완료 - 현재 블랙리스트 토큰 수: {}", blacklistedTokens.size());
    }
}