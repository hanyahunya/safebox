package com.safebox.back.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SessionTokenServiceImpl implements SessionTokenService {

    // 메모리 기반 토큰 저장소 (실제 환경에서는 Redis 등 사용 권장)
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenExpiry = new ConcurrentHashMap<>();

    // 토큰 만료 시간 (24시간)
    private static final long TOKEN_VALIDITY_DURATION = 24 * 60 * 60 * 1000;

    @Override
    public String generateSessionToken(String loginId) {
        // UUID 기반 토큰 생성
        String token = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();

        // Base64 인코딩으로 토큰을 더 안전하게 만들기
        String encodedToken = Base64.getEncoder().encodeToString(
                (loginId + ":" + token + ":" + currentTime).getBytes()
        );

        // 토큰과 사용자 ID 매핑 저장
        tokenStore.put(encodedToken, loginId);
        tokenExpiry.put(encodedToken, currentTime + TOKEN_VALIDITY_DURATION);

        log.info("토큰 생성 완료 - 사용자 ID: {}", loginId);
        return encodedToken;
    }

    @Override
    public boolean validateSessionToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            // 토큰 존재 여부 확인
            if (!tokenStore.containsKey(token)) {
                return false;
            }

            // 토큰 만료 시간 확인
            Long expiryTime = tokenExpiry.get(token);
            if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
                // 만료된 토큰 제거
                invalidateToken(token);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생", e);
            return false;
        }
    }

    @Override
    public String getLoginIdFromToken(String token) {
        if (!validateSessionToken(token)) {
            return null;
        }
        return tokenStore.get(token);
    }

    @Override
    public void invalidateToken(String token) {
        String loginId = tokenStore.remove(token);
        tokenExpiry.remove(token);
        if (loginId != null) {
            log.info("토큰 무효화 완료 - 사용자 ID: {}", loginId);
        }
    }
}