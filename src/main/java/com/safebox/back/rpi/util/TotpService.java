package com.safebox.back.rpi.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TotpService {

    @Value("${totp.secret}")
    private String issueSecret;

    @Value("${totp.verify-secret}")
    private String verifySecret;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String getTotp() {
        int code = gAuth.getTotpPassword(issueSecret);
        return String.format("%06d", code);
    }

    public boolean verifyTotp(String code) {
        return code != null && code.matches("\\d{6}")
                && gAuth.authorize(verifySecret, Integer.parseInt(code));
    }
}
