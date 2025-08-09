package com.safebox.back.rpi.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.jboss.aerogear.security.otp.Totp;

@Service
public class TotpService {

    @Value("${totp.secret}")  // @Value로 TOTP 키를 주입받습니다
    private String key;

    public String getTotp() {
        return new Totp(key).now();
    }
}
