package com.safebox.back.token;

import com.safebox.back.user.Role;

import java.util.Date;

public interface JwtTokenService {
    /**
     * JWT 토큰 생성
     * @param loginId 사용자 로그인 ID
     * @return 생성된 JWT 토큰
     */
    String generateToken(String loginId, Role role);

    /**
     * JWT 토큰 유효성 검증
     * @param token 검증할 토큰
     * @return 유효성 여부
     */
    boolean validateToken(String token);

    /**
     * JWT 토큰에서 로그인 ID 추출
     * @param token JWT 토큰
     * @return 로그인 ID
     */
    String getUserIdFromToken(String token);

    /**
     * JWT 토큰에서 권한 추출
     * @param token JWT 토큰
     * @return Role 객체
     */
    String getRoleFromToken(String token);

    /**
     * JWT 토큰 무효화 (블랙리스트에 추가)
     * @param token 무효화할 토큰
     */
    void invalidateToken(String token);

    /**
     * JWT 토큰 갱신
     * @param token 기존 토큰
     * @return 새로운 토큰
     */
    String refreshToken(String token);

    /**
     * JWT 토큰의 만료 시간 조회
     * @param token JWT 토큰
     * @return 만료 시간
     */
    Date getExpirationDate(String token);

    /**
     * JWT 토큰의 남은 유효 시간 조회 (밀리초)
     * @param token JWT 토큰
     * @return 남은 유효 시간
     */
    long getRemainingValidityTime(String token);
}
