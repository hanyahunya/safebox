package com.safebox.back.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginDto {
    @NotNull(message = "로그인 ID는 필수입니다.")
    @JsonProperty("id")
    private String loginId;

    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;
}