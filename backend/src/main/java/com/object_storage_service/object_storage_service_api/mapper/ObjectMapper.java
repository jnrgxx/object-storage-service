package com.object_storage_service.object_storage_service_api.mapper;

import com.object_storage_service.object_storage_service_api.domain.dto.ObjectResponseDto;
import com.object_storage_service.object_storage_service_api.domain.entity.StorageObject;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapper {

    /**
     * Converts a StorageObject entity (from DB) into an ObjectResponseDto (for API).
     *
     * Notice what we DON'T expose:
     * - 'storagePath' — This is the internal file path on disk. We don't want to
     *   leak server filesystem paths to the client!
     * - 'bucket' — The parent bucket object. If we included it, it could cause
     *   circular references (bucket -> objects -> bucket -> objects...)
     * - 'updatedAt' — Not needed for the response, keeps it clean
     */
    public ObjectResponseDto toDto(StorageObject entity) {
        return new ObjectResponseDto(
                entity.getId(),            // UUID
                entity.getObjectKey(),     // String — the original filename
                entity.getContentType(),   // String — e.g. "image/png"
                entity.getSize(),          // Long — file size in bytes
                entity.getCreatedAt()      // LocalDateTime — when it was uploaded
        );
    }
}