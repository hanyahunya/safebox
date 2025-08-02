package com.safebox.back.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseDto<T> setSuccess(String message, T data) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ResponseDto<T> setFailed(String message) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}