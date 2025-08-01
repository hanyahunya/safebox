package com.safebox.back.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignUpDto {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String loginId;
    @NotNull
    private String password;
}
