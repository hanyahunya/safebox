package com.safebox.back.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final TokenAuthFilter tokenAuthFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); //허용할 URL *****나중에 배포,테스트시에 알맞게 변경********
        configuration.addAllowedMethod("*"); // 허용할 메서드
        configuration.addAllowedHeader("*"); // 허용할 헤더
        configuration.setAllowCredentials(true); // 쿠키등 크레딧 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든요청에 CORS설정 적용
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 허용할 엔드포인트
        final String[] whitelist = {
                "/api/health",
                "/api/user/signup", "/api/user/login"
        };

        http
                .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                        .requestMatchers(whitelist).permitAll()
//                                .anyRequest().permitAll() // --임시설정-- ( 모든 요청 허용 )
                                        .anyRequest().authenticated() // 배포시 주석 해제
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정적용
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class); // 토큰 검증 필터 적용

        return http.build();
    }
}