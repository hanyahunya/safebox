package com.safebox.back.rpi.service;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklist {
    private final ExpiringMap<String, Boolean> blacklist =
            ExpiringMap.builder()
                    .expirationPolicy(ExpirationPolicy.CREATED)
                    .build();

    public void blacklist(String token, long ttlInSeconds) {
        blacklist.put(token, true, ttlInSeconds, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}
