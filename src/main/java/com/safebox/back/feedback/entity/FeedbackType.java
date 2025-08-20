package com.safebox.back.feedback.entity;

public enum FeedbackType {
    BUG("버그 신고"),
    SUGGESTION("개선 제안"),
    COMPLAINT("불만사항"),
    COMPLIMENT("칭찬"),
    QUESTION("문의사항"),
    OTHER("기타");

    private final String description;

    FeedbackType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}