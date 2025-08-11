package com.safebox.back.rpi.service;

import jakarta.servlet.http.HttpServletResponse;

public interface StreamService {
    void stream(HttpServletResponse response, String userId);
}
