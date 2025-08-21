package com.safebox.back.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 피드백 요청 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDto {
    private String productNumber;
    @JsonProperty("phone")
    private String phoneNumber;
    @JsonProperty("complaint")
    private String content;
}