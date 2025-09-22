package com.safebox.back.token;

import io.jsonwebtoken.Claims;

public interface MailTokenService {
    String generateToken(String rpiId, String parcelId);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
