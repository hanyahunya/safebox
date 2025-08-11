package com.safebox.back.rpi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.safebox.back.rpi.entity.StolenDelivery;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
public class StolenDataDto {
    @JsonProperty("rpi_id")
    private String rpiId;

    @JsonProperty("delivery_id")
    private String deliveryId;

    @JsonProperty("arrived_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivedAt;

    @JsonProperty("retrieved_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime retrievedAt;

    public static StolenDataDto entityToDto(StolenDelivery entity) {
        return StolenDataDto.builder()
                .rpiId(entity.getRpi().getRpiId())
                .deliveryId(entity.getDeliveryId())
                .arrivedAt(entity.getArrivedAt())
                .retrievedAt(entity.getRetrievedAt())
                .build();
    }
}
