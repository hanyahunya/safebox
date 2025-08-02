package com.safebox.back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API이므로)
                .csrf(csrf -> csrf.disable())

                // 세션 사용하지 않음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증/인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/signup", "/api/user/login").permitAll() // 회원가입, 로그인은 모든 사용자 허용
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 허용 (개발 환경)
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // 기본 로그인 폼 비활성화
                .formLogin(form -> form.disable())

                // HTTP Basic 인증 비활성화
                .httpBasic(basic -> basic.disable())

                // H2 콘솔을 위한 헤더 설정
                .headers(headers -> headers
                        .frameOptions().disable()
                );

        return http.build();
    }
}