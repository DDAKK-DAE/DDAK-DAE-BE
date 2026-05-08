package com.hackathon.api.domain.file.controller;

import com.hackathon.api.domain.file.dto.FileUploadResponse;
import com.hackathon.api.domain.file.service.FileUploadService;
import com.hackathon.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Files", description = "이미지 / 동영상 업로드")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Operation(summary = "파일 업로드", description = "이미지(프로필) 또는 릴스 동영상을 업로드하고 저장된 URL을 반환합니다.")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResponse<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        String url = fileUploadService.store(file);
        return ApiResponse.ok(new FileUploadResponse(url));
    }
}
