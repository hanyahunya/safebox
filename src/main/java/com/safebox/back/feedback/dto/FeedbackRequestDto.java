package com.safebox.back.feedback.dto;

import com.safebox.back.feedback.entity.FeedbackType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FeedbackRequestDto {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이내여야 합니다")
    private String name;

    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    private String email;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이내여야 합니다")
    private String subject;

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 2000, message = "내용은 2000자 이내여야 합니다")
    private String content;

    @NotNull(message = "피드백 타입은 필수입니다")
    private FeedbackType type;

    // 기본 생성자
    public FeedbackRequestDto() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public FeedbackType getType() { return type; }
    public void setType(FeedbackType type) { this.type = type; }
}
