package com.safebox.back.member.dto;

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
    private String userId;
    @NotNull
    private String password;
}
