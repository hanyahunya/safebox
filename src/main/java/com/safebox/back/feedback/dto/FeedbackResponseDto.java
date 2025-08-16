package com.safebox.back.feedback.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import com.safebox.back.feedback.entity.FeedbackStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 피드백 응답 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FeedbackResponseDto {

    private UUID id;
    private String productNumber;
    private String phoneNumber;
    private String content;
    private FeedbackStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String adminReply;
    private LocalDateTime repliedAt;
}