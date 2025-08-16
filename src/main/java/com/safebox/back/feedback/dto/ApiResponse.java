package com.safebox.back.feedback.dto;

public class ApiResponse<T> {

    private boolean success; // api 호출 성공, 실패
    private String message; // 클라이언트에게 전송할 메세지
    private T data; // 응답 데이터 받는 필드

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    // 모든 필드를 한 번에 초기화 하는 필드

    // 성공 : true
    // "답변이 성공적으로 등록되었습니다."
    // adminReplyDto

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}