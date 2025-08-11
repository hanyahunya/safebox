package com.safebox.back.rpi.service;

import com.safebox.back.rpi.dto.StolenDataListDto;
import com.safebox.back.rpi.dto.StolenVideoDto;
import com.safebox.back.util.ResponseDto;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;

public interface StolenService {
    ResponseDto<Void> saveStolen(StolenVideoDto reqDto);

    ResponseDto<StolenDataListDto> getStolenDataList(String userId);

    ResponseEntity<ResourceRegion> getVideoRegion(String range);
}
