package com.safebox.back.user.service;

import com.safebox.back.token.JwtTokenService;
import com.safebox.back.token.JwtTokenException;
import com.safebox.back.user.dto.LoginDto;
import com.safebox.back.user.dto.SignUpDto;
import com.safebox.back.user.entity.User;
import com.safebox.back.user.repository.UserRepository;
import com.safebox.back.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService; // 변경: JWT 기반 서비스 사용

    @Override
    public ResponseDto<Void> signUp(SignUpDto signUpDto) {
        try {
            // 1. 로그인 ID 중복 체크
            if (userRepository.existsByLoginId(signUpDto.getLoginId())) {
                log.warn("회원가입 실패 - 중복된 로그인 ID: {}", signUpDto.getLoginId());
                return ResponseDto.fail("이미 존재하는 아이디입니다.");
            }

            // 2. 이메일 중복 체크
            if (userRepository.existsByEmail(signUpDto.getEmail())) {
                log.warn("회원가입 실패 - 중복된 이메일: {}", signUpDto.getEmail());
                return ResponseDto.fail("이미 존재하는 이메일입니다.");
            }

            // 3. 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());

            // 4. 엔티티 생성 및 저장
            User user = User.builder()
                    .name(signUpDto.getName())
                    .email(signUpDto.getEmail())
                    .loginId(signUpDto.getLoginId())
                    .password(encodedPassword)
                    .build();

            userRepository.save(user);
            log.info("회원가입 성공 - 사용자 ID: {}", signUpDto.getLoginId());

            return ResponseDto.success("회원가입이 완료되었습니다.");

        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생", e);
            return ResponseDto.fail("회원가입 처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<String> login(LoginDto loginDto) {
        try {
            // 1. 사용자 조회
            User user = userRepository.findByLoginId(loginDto.getLoginId())
                    .orElse(null);

            if (user == null) {
                log.warn("로그인 실패 - 존재하지 않는 사용자: {}", loginDto.getLoginId());
                return ResponseDto.fail("존재하지 않는 아이디입니다.");
            }

            // 2. 비밀번호 검증
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                log.warn("로그인 실패 - 비밀번호 불일치: {}", loginDto.getLoginId());
                return ResponseDto.fail("비밀번호가 일치하지 않습니다.");
            }

            // 3. JWT 토큰 생성
            String jwtToken = jwtTokenService.generateToken(user.getLoginId());
            log.info("로그인 성공 - 사용자 ID: {}", loginDto.getLoginId());

            return ResponseDto.success("로그인 성공", jwtToken);

        } catch (JwtTokenException e) {
            log.error("JWT 토큰 생성 중 오류 - 사용자 ID: {}", loginDto.getLoginId(), e);
            return ResponseDto.fail("로그인 토큰 생성 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            return ResponseDto.fail("로그인 처리 중 오류가 발생했습니다.");
        }
    }
}
