package com.object_storage_service.object_storage_service_api.service;

import com.object_storage_service.object_storage_service_api.domain.dto.ObjectResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.UUID;

/**
 * Business logic for managing objects (files) inside buckets.
 */
public interface ObjectService {
    /**
     * Uploads a file to a bucket.
     *
     * @param bucketId the ID of the bucket to upload to
     * @param file     the uploaded file (from the HTTP request)
     * @return metadata about the stored object
     */
    ObjectResponseDto uploadObject(UUID bucketId, MultipartFile file);

    /**
     * Lists all objects in a bucket (metadata only, not the files themselves).
     */
    List<ObjectResponseDto> listObjects(UUID bucketId);

    /**
     * Gets metadata for a single object.
     */
    ObjectResponseDto getObjectMetadata(UUID bucketId, UUID objectId);

    /**
     * Downloads the actual file content as a Resource.
     */
    Resource downloadObject(UUID bucketId, UUID objectId);

    /**
     * Deletes an object from the database and its file from disk.
     */
    void deleteObject(UUID bucketId, UUID objectId);
}
