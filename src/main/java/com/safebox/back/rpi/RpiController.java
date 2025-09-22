package com.safebox.back.rpi;

import com.safebox.back.mail.MailService;
import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.rpi.dto.RpiParcelUuidDto;
import com.safebox.back.rpi.dto.StolenDataListDto;
import com.safebox.back.rpi.dto.StolenVideoDto;
import com.safebox.back.rpi.service.RpiService;
import com.safebox.back.rpi.service.StolenService;
import com.safebox.back.rpi.service.StreamService;
import com.safebox.back.rpi.util.TotpService;
import com.safebox.back.security.StolenPrincipal;
import com.safebox.back.security.UserPrincipal;
import com.safebox.back.token.JwtTokenService;
import com.safebox.back.util.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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
    private final MailService mailService;


    // -------------------------for user-------------------------
    @GetMapping("/video/{token}")
    public void liveStream(HttpServletResponse response, @PathVariable("token") String token) {
        if (!tokenService.validateToken(token)) {
            return;
        }
        String userId = tokenService.getUserIdFromToken(token);
        streamService.stream(response, userId);
    }

    @GetMapping("/stolen/{rpi_id}/{delivery_id}")
    public ResponseEntity<Resource> stolenVideo(@PathVariable("rpi_id")String rpiId, @PathVariable("delivery_id")String deliId, @AuthenticationPrincipal UserPrincipal user) {
        return stolenService.getVideo(user.getUserId(), rpiId, deliId);
    }

    @GetMapping("/stolen")
    public ResponseEntity<ResponseDto<StolenDataListDto>> getStolenList(@AuthenticationPrincipal UserPrincipal user) {
        ResponseDto<StolenDataListDto> responseDto = stolenService.getStolenDataList(user.getUserId());
        return toResponse(responseDto);
    }

    // -------------------------for rpi-------------------------
    @PostMapping("/arrived/{rpi_uuid}/{parcel_uuid}/{otp}")
    public ResponseEntity<ResponseDto<Void>> arrived(@PathVariable("rpi_uuid") String rpiUuid, @PathVariable("parcel_uuid") String parcelUuid, @PathVariable("otp") String otp) {
        log.info("arrived {}", rpiUuid);
        if (!totpService.verifyTotp(otp)) {
            return ResponseEntity.status(403).build();
        }

        ResponseDto<Void> responseDto = rpiService.arrived(RpiParcelUuidDto.builder().rpiUuid(rpiUuid).parcelUuid(parcelUuid).build());
        return toResponse(responseDto);
    }


    @PostMapping("/pickuped/{rpi_uuid}/{parcel_uuid}/{otp}")
    public ResponseEntity<ResponseDto<Void>> pickuped(@PathVariable(name = "rpi_uuid") String rpiUuid, @PathVariable(name = "parcel_uuid") String parcelUuid, @PathVariable("otp") String otp) {
        log.info("pickuped {}", parcelUuid);
        if (!totpService.verifyTotp(otp)) {
            return ResponseEntity.status(400).build();
        }
        ResponseDto<Void> responseDto = rpiService.pickuped(RpiParcelUuidDto.builder().rpiUuid(rpiUuid).parcelUuid(parcelUuid).build());
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


    // -------------------------for admin-------------------------
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
