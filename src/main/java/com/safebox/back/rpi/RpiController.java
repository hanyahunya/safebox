package com.safebox.back.rpi;

import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.rpi.service.RpiService;
import com.safebox.back.rpi.util.TotpService;
import com.safebox.back.util.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.safebox.back.util.ResponseUtil.toResponse;

@RestController
@RequestMapping("/api/rpi")
@RequiredArgsConstructor
public class RpiController {
    private final RpiService rpiService;
    private final TotpService totpService;

    @PostMapping("/arrived/{rpi_uuid}/{parcel_uuid}/{otp}")
    public ResponseEntity<Void> arrived(@PathVariable("rpi_uuid") String rpiUuid, @PathVariable("parcel_uuid") String parcelUuid, @PathVariable("otp") String otp) {
        if (!totpService.verifyTotp(otp)) {
            return ResponseEntity.status(400).build();
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
