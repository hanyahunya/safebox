package com.safebox.back.user.service;

public interface SessionTokenService {
    /**
     * 세션 토큰 생성
     * @param loginId 사용자 로그인 ID
     * @return 생성된 토큰
     */
    String generateSessionToken(String loginId);

    /**
     * 토큰 유효성 검증
     * @param token 검증할 토큰
     * @return 유효성 여부
     */
    boolean validateSessionToken(String token);

    /**
     * 토큰에서 로그인 ID 추출
     * @param token 토큰
     * @return 로그인 ID
     */
    String getLoginIdFromToken(String token);

    /**
     * 토큰 무효화
     * @param token 무효화할 토큰
     */
    void invalidateToken(String token);
}