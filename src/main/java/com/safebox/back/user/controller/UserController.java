package com.safebox.back.user.controller;

import com.safebox.back.user.dto.LoginDto;
import com.safebox.back.user.dto.SignUpDto;
import com.safebox.back.user.service.UserService;
import com.safebox.back.util.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Void>> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        log.info("회원가입 요청 - 로그인 ID: {}", signUpDto.getLoginId());
        ResponseDto<Void> response = userService.signUp(signUpDto);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<String>> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("로그인 요청 - 로그인 ID: {}", loginDto.getLoginId());
        ResponseDto<String> response = userService.login(loginDto);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 로그아웃 (토큰 무효화)
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // 여기서 토큰 무효화 로직 구현 가능
                log.info("로그아웃 요청");
                return ResponseEntity.ok(ResponseDto.setSuccess("로그아웃 되었습니다.", null));
            }
            return ResponseEntity.badRequest().body(ResponseDto.setFailed("유효하지 않은 토큰입니다."));
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ResponseDto.setFailed("로그아웃 처리 중 오류가 발생했습니다."));
        }
    }
}
