package com.safebox.back.rpi;

import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.rpi.dto.StolenDataListDto;
import com.safebox.back.rpi.dto.StolenVideoDto;
import com.safebox.back.rpi.service.RpiService;
import com.safebox.back.rpi.service.StolenService;
import com.safebox.back.rpi.service.StreamService;
import com.safebox.back.rpi.util.TotpService;
import com.safebox.back.security.UserPrincipal;
import com.safebox.back.token.JwtTokenService;
import com.safebox.back.util.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.safebox.back.util.ResponseUtil.toResponse;

@Slf4j
@RestController
@RequestMapping("/api/rpi")
@RequiredArgsConstructor
public class RpiController {
    private final RpiService rpiService;
    private final TotpService totpService;
    private final StolenService stolenService;
    private final StreamService streamService;
    private final JwtTokenService tokenService;

//    @GetMapping("/video")
//    public void proxyMjpegStream(HttpServletResponse response) {
//        String videoStreamUrl = "http://safebox-rssh:58219/video";  // 외부 MJPEG 스트림 URL
//
//        try {
//            // URI로 변환
//            URI uri = URI.create(videoStreamUrl);
//
//            // HttpClient로 MJPEG 스트림 요청
//            HttpClient client = HttpClient.newHttpClient();
//            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
//            HttpResponse<InputStream> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
//
//            // HTTP 응답의 Content-Type을 'multipart/x-mixed-replace'로 설정
//            response.setContentType("multipart/x-mixed-replace; boundary=frame");
//
//            try (InputStream inputStream = httpResponse.body();
//                 OutputStream outputStream = response.getOutputStream()) {
//
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                boolean firstFrame = true;
//
//                // 스트리밍 데이터를 클라이언트로 전달
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    if (firstFrame) {
//                        // 첫 번째 프레임에 대해서만 boundary와 Content-Type, Content-Length 처리
//                        outputStream.write("--frame\r\n".getBytes());  // boundary 구분자
//                        outputStream.write("Content-Type: image/jpeg\r\n".getBytes());  // 첫 번째 프레임의 Content-Type
//                        outputStream.write("Content-Length: ".getBytes());  // Content-Length
//                        outputStream.write(String.valueOf(bytesRead).getBytes());  // 첫 번째 프레임의 크기
//                        outputStream.write("\r\n\r\n".getBytes());  // 첫 번째 프레임 헤더 끝
//                        firstFrame = false;
//                    } else {
//                        // 첫 번째 프레임 이후의 프레임에서는 header를 건너뛰고 이미지 데이터만 전송
//                        outputStream.write("--frame\r\n".getBytes());  // boundary 구분자
//                        outputStream.write("Content-Type: image/jpeg\r\n\r\n".getBytes());  // Content-Type만 전송
//                    }
//
////                     이미지 데이터 전송
//                    outputStream.write(buffer, 0, bytesRead);  // 이미지 데이터
//                    outputStream.write("\r\n".getBytes());  // boundary 구분자 뒤에 빈 줄 추가
//                    outputStream.flush();  // 실시간으로 클라이언트에 전송
//                }
//            }
//        } catch (Exception e) {
//            return;
//        }
//    }

    @GetMapping("/video/{token}")
    public void proxyMjpegStream(HttpServletResponse response, @PathVariable("token") String token) {
        if (!tokenService.validateToken(token)) {
            return;
        }
        String userId = tokenService.getUserIdFromToken(token);
        streamService.stream(response, userId);
    }



    @GetMapping("/stolen")
    public ResponseEntity<ResponseDto<StolenDataListDto>> getStolenList(@AuthenticationPrincipal UserPrincipal user) {
        ResponseDto<StolenDataListDto> responseDto = stolenService.getStolenDataList(user.getUserId());
        return toResponse(responseDto);
    }

    @PostMapping("/stolen/{rpi_uuid}/{otp}")
    public ResponseEntity<ResponseDto<Void>> markStolen(
            @PathVariable("rpi_uuid") String rpiUuid,
            @PathVariable("otp") String otp,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("uuid") String deliUuid,
            @RequestParam("arrived_at") String arrivedAt,
            @RequestParam("retrieved_at") String retrievedAt
            ) {
        if (!totpService.verifyTotp(otp)) {
            return ResponseEntity.status(403).build();
        }
        return toResponse(stolenService.saveStolen(StolenVideoDto.builder().rpiUuid(rpiUuid).deliUuid(deliUuid).arrivedAt(arrivedAt).retrievedAt(retrievedAt).videoFile(videoFile).build()));
    }

    @PostMapping("/arrived/{rpi_uuid}/{parcel_uuid}/{otp}")
    public ResponseEntity<Void> arrived(@PathVariable("rpi_uuid") String rpiUuid, @PathVariable("parcel_uuid") String parcelUuid, @PathVariable("otp") String otp) {
        if (!totpService.verifyTotp(otp)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/pickuped/{rpi_uuid}/{parcel_uuid}/{otp}")
    public ResponseEntity<Void> pickuped(@PathVariable(name = "rpi_uuid") String rpiUuid, @PathVariable(name = "parcel_uuid") String parcelUuid, @PathVariable("otp") String otp) {
        if (!totpService.verifyTotp(otp)) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<String>> addRpi(@RequestBody @Valid AddRpiDto addRpiDto) {
        return toResponse(rpiService.addUser(addRpiDto));
    }

    @DeleteMapping("/{rpi_uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Void>> deleteRpi(@PathVariable(name = "rpi_uuid") String rpi_uuid) {
        return toResponse(rpiService.deleteUser(rpi_uuid));
    }
}
