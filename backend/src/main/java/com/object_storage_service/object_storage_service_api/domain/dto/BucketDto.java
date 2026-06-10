package com.object_storage_service.object_storage_service_api.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BucketDto(
        UUID id,
        String name,
        LocalDateTime createdAt,
        long objectCount
) {
}
