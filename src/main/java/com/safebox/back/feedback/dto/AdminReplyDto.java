package com.safebox.back.feedback.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * 관리자 답변 DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminReplyDto {

    @NotBlank(message = "답변 내용은 필수입니다")
    private String reply;
}