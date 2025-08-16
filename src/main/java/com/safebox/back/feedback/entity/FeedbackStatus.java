package com.safebox.back.feedback.entity;

public enum FeedbackStatus {
    PENDING("대기중"),
    IN_PROGRESS("처리중"),
    RESOLVED("해결완료"),
    CLOSED("종료");

    private final String description;

    FeedbackStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}