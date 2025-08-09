package com.safebox.back.rpi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddRpiDto {
    @JsonIgnore
    private String username;
    @NotNull
    private String port;
    @NotNull
    private String pubkey;
}
