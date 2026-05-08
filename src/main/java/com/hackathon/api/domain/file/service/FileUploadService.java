package com.hackathon.api.domain.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 로컬 파일시스템 기반 업로드 서비스.
 * S3 미연동 데모 환경에서 ./uploads 디렉토리에 파일을 저장하고 접근 가능한 URL을 반환한다.
 * 운영 환경에서는 UPLOAD_PATH, UPLOAD_BASE_URL 환경변수로 경로와 베이스 URL을 주입한다.
 */
@Service
public class FileUploadService {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png", ".webp", ".mp4", ".mov");
    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "video/mp4", "video/quicktime");

    private final Path uploadDir;
    private final String baseUrl;

    public FileUploadService(
            @Value("${app.upload.path}") String uploadPath,
            @Value("${app.upload.base-url}") String baseUrl) throws IOException {
        this.baseUrl = baseUrl;
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        // 애플리케이션 기동 시 업로드 디렉토리가 없으면 자동 생성
        Files.createDirectories(this.uploadDir);
    }

    /**
     * 파일을 저장하고 접근 URL을 반환한다.
     * 원본 파일명 대신 UUID를 사용해 충돌과 경로 조작 공격을 방지한다.
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : "";
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("허용되지 않은 확장자입니다.");
        }
        String filename = UUID.randomUUID() + ext;

        try {
            Path target = uploadDir.resolve(filename);
            file.transferTo(target);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }

        return baseUrl + "/uploads/" + filename;
    }
}
