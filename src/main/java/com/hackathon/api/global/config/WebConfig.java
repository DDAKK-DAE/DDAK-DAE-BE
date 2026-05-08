package com.hackathon.api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * MVC 정적 리소스 설정.
 * /uploads/** 요청을 로컬 파일시스템의 업로드 디렉토리로 매핑한다.
 * SecurityConfig에서 GET /uploads/** 를 permitAll 처리하므로 인증 없이 접근 가능하다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 상대 경로를 절대 URI로 변환해야 Spring ResourceHandler가 파일시스템을 인식한다
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
