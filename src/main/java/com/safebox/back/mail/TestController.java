package com.safebox.back.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final MailService mailService;

    @GetMapping
    public String test() {
        mailService.sendArrivedMail("gkals020103@gmail.com", "제목", "ArrivedMail", Map.of());
        return "ok";
    }
}
