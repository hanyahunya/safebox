package com.safebox.back.rpi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter @Setter
public class StolenVideoDto {
    private MultipartFile videoFile;
    private String rpiUuid;
    private String deliUuid;
    private String arrivedAt;
    private String retrievedAt;
}
