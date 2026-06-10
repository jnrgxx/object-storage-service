package com.object_storage_service.object_storage_service_api.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ObjectResponseDto(
        UUID id,
        String objectKey,
        String contentType,
        long size,
        LocalDateTime createdAt
) {
}
