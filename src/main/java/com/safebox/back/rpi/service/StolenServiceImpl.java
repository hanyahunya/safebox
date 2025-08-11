package com.safebox.back.rpi.service;

import com.safebox.back.rpi.dto.StolenDataListDto;
import com.safebox.back.rpi.dto.StolenVideoDto;
import com.safebox.back.rpi.entity.Rpi;
import com.safebox.back.rpi.entity.StolenDelivery;
import com.safebox.back.rpi.repository.StolenRepository;
import com.safebox.back.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StolenServiceImpl implements StolenService {
    private final StolenRepository stolenRepository;

    @Override
    public ResponseDto<Void> saveStolen(StolenVideoDto reqDto) {

        String rpiUuid = reqDto.getRpiUuid();
        String deliUuid = reqDto.getDeliUuid();
        String arrivedAt = reqDto.getArrivedAt();
        String retrievedAt = reqDto.getRetrievedAt();
        MultipartFile videoFile = reqDto.getVideoFile();


        try {
            Path saveDir = Paths.get("/app/videos");
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }

            // 확장자 추출
            String originalName = videoFile.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String saveFileName = rpiUuid + "---" + deliUuid + extension;
            Path savePath = saveDir.resolve(saveFileName);

            Files.copy(videoFile.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);


        } catch (IOException e) {
            return ResponseDto.fail("영상저장 실패");
        }

        StolenDelivery stolenDelivery = StolenDelivery.builder()
                .deliveryId(deliUuid)
                .rpi(Rpi.builder().rpiId(rpiUuid).build())
                .arrivedAt(LocalDateTime.parse(arrivedAt))
                .retrievedAt(LocalDateTime.parse(retrievedAt))
                .build();
        try {
            stolenRepository.save(stolenDelivery);
            return ResponseDto.success("저장 성공");
        } catch (DataIntegrityViolationException e) {
            return ResponseDto.fail("무결성 위반");
        } catch (Exception e) {
            return ResponseDto.fail("저장 실패");
        }
    }

    @Override
    public ResponseDto<StolenDataListDto> getStolenDataList(String userId) {
        StolenDataListDto responseDto = StolenDataListDto.entityToDto(stolenRepository.findByRpi_User_Id(userId));
        return ResponseDto.success("stolen 정보 불러오기 설공", responseDto);
    }



    @Override
    public ResponseEntity<ResourceRegion> getVideoRegion(String rangeHeader) {
        Path path = Path.of("/app/videos/test.mp4");
        try {
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            UrlResource resource = new UrlResource(path.toUri());
            long contentLength = resource.contentLength();

            MediaType mediaType = MediaTypeFactory.getMediaType(path.getFileName().toString())
                    .orElse(MediaType.asMediaType(MimeTypeUtils.APPLICATION_OCTET_STREAM));

            // Range 요청 처리
            if (rangeHeader != null) {
                var ranges = HttpRange.parseRanges(rangeHeader);
                if (ranges.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + contentLength)
                            .build();
                }
                HttpRange range = ranges.get(0);

                long start = range.getRangeStart(contentLength);
                long end = range.getRangeEnd(contentLength);
                if (start < 0 || end < start || end >= contentLength) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + contentLength)
                            .build();
                }

                ResourceRegion region = range.toResourceRegion(resource);
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .contentType(mediaType)
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(region);
            }

            // Range가 없으면 전체 구간
            ResourceRegion whole = new ResourceRegion(resource, 0, contentLength);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(whole);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
