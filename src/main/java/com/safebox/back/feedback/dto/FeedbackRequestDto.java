package com.safebox.back.feedback.dto;

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

    @NotBlank(message = "제품번호는 필수입니다")
    private String productNumber;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^[0-9-+()\\s]+$", message = "올바른 전화번호 형식이 아닙니다")
    private String phoneNumber;

    @NotBlank(message = "내용은 필수입니다")
    private String content;
}