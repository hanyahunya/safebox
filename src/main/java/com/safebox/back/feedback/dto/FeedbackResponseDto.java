package com.safebox.back.feedback.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import com.safebox.back.feedback.entity.FeedbackStatus;
import com.safebox.back.feedback.entity.FeedbackType;
import java.time.LocalDateTime;

/**
 * 피드백 응답 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FeedbackResponseDto {

    private String id;  // UUID에서 String으로 변경
    private String userId;  // 사용자 ID 추가
    private String productNumber;
    private String phoneNumber;
    private String content;
    private FeedbackStatus status;
    private FeedbackType type;  // 타입 필드 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String adminReply;
    private LocalDateTime repliedAt;
}