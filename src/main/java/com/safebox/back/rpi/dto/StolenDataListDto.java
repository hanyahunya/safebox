package com.safebox.back.rpi.dto;

import com.safebox.back.rpi.entity.StolenDelivery;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter @Setter
public class StolenDataListDto {

    private List<StolenDataDto> stolenDataList;

    public static StolenDataListDto entityToDto(List<StolenDelivery> entities) {
        List<StolenDataDto> stolenDataDtoList = entities.stream()
                .map(StolenDataDto::entityToDto)
                .collect(Collectors.toList());

        return StolenDataListDto.builder()
                .stolenDataList(stolenDataDtoList)
                .build();
    }
}