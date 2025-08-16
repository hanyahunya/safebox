package com.safebox.back.feedback.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class DatabaseConfig {

    /**
     * 기본 페이징 설정
     */
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customize() {
        return p -> {
            p.setOneIndexedParameters(false); // 0부터 시작
            p.setMaxPageSize(100); // 최대 페이지 크기
            p.setFallbackPageable(PageRequest.of(0, 10)); // 기본 페이지 설정
        };
    }
}