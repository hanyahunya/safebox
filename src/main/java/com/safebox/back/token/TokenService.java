package com.safebox.back.token;

import io.jsonwebtoken.Claims;

public interface TokenService {
    /**
     * 토큰 발급 메서드
     * @param userId 유저 고유 uuid
     * @return 토큰 문자열
     */
    String generateToken(String userId);

    /**
     * 토큰 검증 메서드
     * @param token 토큰 문자열
     * @return 검증결과
     */
    boolean validateToken(String token);

    /**
     * 토큰의 claim 들을 불러오는 메서드
     * @param token 토큰
     * @return claim 들을 포함하고있는 Claims 객체를 반환
     */
    Claims getClaims(String token);
}
