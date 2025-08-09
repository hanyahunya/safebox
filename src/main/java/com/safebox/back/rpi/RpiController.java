package com.safebox.back.rpi;

import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.rpi.service.RpiService;
import com.safebox.back.util.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.safebox.back.util.ResponseUtil.toResponse;

@RestController
@RequestMapping("/api/rpi")
@RequiredArgsConstructor
public class RpiController {
    private final RpiService rpiService;

    @PostMapping("/arrived/{rpi_uuid}/{parcel_uuid}")
    public ResponseEntity<Void> arrived(@PathVariable(name = "rpi_uuid") String rpi_uuid, @PathVariable(name = "parcel_uuid") String parcel_uuid) {
        return null;
    }

    @PostMapping("/pickuped/{rpi_uuid}/{parcel_uuid}")
    public ResponseEntity<Void> pickuped(@PathVariable(name = "rpi_uuid") String rpi_uuid, @PathVariable(name = "parcel_uuid") String parcel_uuid) {
        return null;
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
