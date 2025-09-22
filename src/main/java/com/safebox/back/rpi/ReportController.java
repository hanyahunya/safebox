package com.safebox.back.rpi;

import com.safebox.back.rpi.service.StolenService;
import com.safebox.back.rpi.service.TokenBlacklist;
import com.safebox.back.security.StolenPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReportController {
    private final StolenService stolenService;
    private final TokenBlacklist tokenBlacklist;

    @GetMapping("/report/{token}")
    public String reportStolen(@PathVariable("token")String token, @AuthenticationPrincipal StolenPrincipal principal) {
        if (tokenBlacklist.isBlacklisted(token)) {
            return "stolen/usedToken";
        }
        long ttl = (principal.getExpirationDate().getTime() - System.currentTimeMillis()) / 1000;
        tokenBlacklist.blacklist(token, ttl);
        if (stolenService.reportStolen(principal.getRpiId(), principal.getParcelId())) {
            return "stolen/complete";
        } else {
            return "stolen/failed";
        }
    }
}
