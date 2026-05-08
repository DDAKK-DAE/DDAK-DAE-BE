package com.hackathon.api.domain.ai.dto;

import java.util.List;

public record GenerateDescriptionResponse(String description, List<String> hashtags) {}
