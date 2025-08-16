package com.safebox.back.rpi;

import com.safebox.back.rpi.service.StolenService;
import com.safebox.back.security.StolenPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReportController {
    private final StolenService stolenService;

    @GetMapping("/report/{token}")
    public String reportStolen(@AuthenticationPrincipal StolenPrincipal principal) {
        if (stolenService.reportStolen(principal.getRpiId(), principal.getParcelId())) {
            return "stolen/complete";
        } else {
            return "stolen/failed";
        }
    }
}
