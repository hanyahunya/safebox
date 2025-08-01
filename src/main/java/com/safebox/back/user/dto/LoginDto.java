package com.safebox.back.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginDto {
    @NotNull
    private String loginId;
    @NotNull
    private String password;
}
