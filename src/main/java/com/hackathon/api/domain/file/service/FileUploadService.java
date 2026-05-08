package com.hackathon.api.domain.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Path uploadDir;
    private final String baseUrl;

    public FileUploadService(
            @Value("${app.upload.path}") String uploadPath,
            @Value("${app.upload.base-url}") String baseUrl) throws IOException {
        this.baseUrl = baseUrl;
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    public String store(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String ext = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : "";
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
