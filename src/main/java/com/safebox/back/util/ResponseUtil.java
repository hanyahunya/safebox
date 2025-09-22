package com.safebox.back.util;

import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    private ResponseUtil() {
        throw new UnsupportedOperationException("이 클래스는 인스턴스화 할수없습니다.");
    }

    public static <T> ResponseEntity<ResponseDto<T>> toResponse(ResponseDto<T> responseDto) {
        if (responseDto.isSuccess()) {
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.badRequest().body(responseDto);
        }
    }
}