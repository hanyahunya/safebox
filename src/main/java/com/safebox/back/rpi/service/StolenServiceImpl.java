package com.safebox.back.rpi.service;

import com.safebox.back.rpi.dto.StolenDataListDto;
import com.safebox.back.rpi.dto.StolenVideoDto;
import com.safebox.back.rpi.entity.Rpi;
import com.safebox.back.rpi.entity.StolenDelivery;
import com.safebox.back.rpi.repository.RpiRepository;
import com.safebox.back.rpi.repository.StolenRepository;
import com.safebox.back.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StolenServiceImpl implements StolenService {
    private final StolenRepository stolenRepository;
    private final RpiRepository rpiRepository;

    private final RestTemplate restTemplate = new RestTemplate();;

    @Override
    public ResponseDto<StolenDataListDto> getStolenDataList(String userId) {
        StolenDataListDto responseDto = StolenDataListDto.entityToDto(stolenRepository.findByRpi_User_Id(userId));
        return ResponseDto.success("stolen 정보 불러오기 설공", responseDto);
    }

    @Override
    public ResponseEntity<Resource> getVideo(String userId, String rpiId, String deliveryId) {

        if ( !stolenRepository.existsByDeliveryIdAndRpi_RpiIdAndRpi_User_Id(deliveryId, rpiId, userId)) {
            return ResponseEntity.notFound().build();
        }


        Path path = Path.of("/app/videos/" + rpiId + "---" + deliveryId + ".mp4");

        if (!Files.exists(path) || !Files.isReadable(path)) {
            return ResponseEntity.notFound().build();
        }

        FileSystemResource resource = new FileSystemResource(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Override
    public boolean reportStolen(String rpiId, String parcelId) {
        Optional<Rpi> rpiOpt = rpiRepository.findById(rpiId);
        if (rpiOpt.isEmpty()) {
            return false;
        }

        String port = rpiOpt.get().getPort();
        String url = "http://safebox-rssh:" + port + "/stolen/" + parcelId;
        log.info(url);
        log.info(url);

        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ResponseDto<Void> saveStolen(StolenVideoDto reqDto) {

        String rpiUuid   = reqDto.getRpiUuid();
        String deliUuid  = reqDto.getDeliUuid();
        String arrivedAt = reqDto.getArrivedAt();
        String retrievedAt = reqDto.getRetrievedAt();
        MultipartFile videoFile = reqDto.getVideoFile();

        Path saveDir = Paths.get("/app/videos");

        String finalFileName = rpiUuid + "---" + deliUuid + ".mp4";
        Path finalPath = saveDir.resolve(finalFileName);

        // 임시
        Path tmpIn  = null;
        Path tmpOut = null;

        try {
            if (!Files.exists(saveDir)) Files.createDirectories(saveDir);

            tmpIn = Files.createTempFile(saveDir, "upload-", ".bin");
            try (var in = videoFile.getInputStream()) {
                Files.copy(in, tmpIn, StandardCopyOption.REPLACE_EXISTING);
            }

            // 2) ffmpeg로 H.264 + AAC + faststart 변환
            tmpOut = Files.createTempFile(saveDir, "transcode-", ".mp4");
            boolean ok = transcodeToH264(tmpIn, tmpOut);
            if (!ok) {
                log.warn("[saveStolen] ffmpeg transcodeToH264 실패 → return fail()");
                tryDelete(tmpOut);
                return ResponseDto.fail("트랜스코딩 실패 (ffmpeg 필요)");
            }

            try {
                Files.move(tmpOut, finalPath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignore) {
                Files.move(tmpOut, finalPath, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            log.warn("[saveStolen] IOException 발생, 영상저장 실패");
            return ResponseDto.fail("영상저장 실패");
        } finally {
            tryDelete(tmpIn);
            tryDelete(tmpOut);
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
        } catch (Exception e) {
            log.warn("[saveStolen] DB save 실패 ({} , {})", rpiUuid, deliUuid);
            return ResponseDto.fail("저장 실패");
        }
    }

    /** ffmpeg로 H.264(AVC) + AAC + faststart 트랜스코딩 */
    private boolean transcodeToH264(Path in, Path out) {
        List<String> cmd = Arrays.asList(
                "ffmpeg", "-y",
                "-i", in.toString(),
                "-map", "0:v:0", "-c:v", "libx264", "-preset", "veryfast", "-crf", "22",
                "-pix_fmt", "yuv420p",
                "-map", "0:a:0?", "-c:a", "aac", "-b:a", "128k",
                "-movflags", "+faststart",
                out.toString()
        );
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            // 로그 소비(버퍼 꽉 차서 대기하는 걸 방지)
            try (var r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                while (r.readLine() != null) {  } // <--버퍼역할 매우중요
            }
            boolean finished = p.waitFor(10, TimeUnit.MINUTES);
            if (!finished) {
                p.destroyForcibly();
                return false;
            }
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void tryDelete(Path p) {
        if (p == null) return;
        try { Files.deleteIfExists(p); } catch (Exception ignore) {}
    }
}
