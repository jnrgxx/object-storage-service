package com.object_storage_service.object_storage_service_api.controller;

import com.object_storage_service.object_storage_service_api.domain.dto.ObjectResponseDto;
import com.object_storage_service.object_storage_service_api.service.ObjectService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buckets/{bucketId}/objects")
public class ObjectController {

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    /**
     * POST /api/v1/buckets/{bucketId}/objects
     * Uploads a file to the specified bucket.
     * The file is sent as multipart/form-data with field name "file".
     * Response: 201 Created with object metadata
     */
    @PostMapping
    public ResponseEntity<ObjectResponseDto> uploadObject(
            @PathVariable UUID bucketId,
            @RequestParam("file")MultipartFile file) {

        ObjectResponseDto response = objectService.uploadObject(bucketId, file);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * GET /api/v1/buckets/{bucketId}/objects
     * Lists all objects (metadata) in a bucket.
     * Response: 200 OK with list of object metadata
     */
    @GetMapping
    public ResponseEntity<List<ObjectResponseDto>> listObjects(@PathVariable UUID bucketId) {
        return ResponseEntity.ok(objectService.listObjects(bucketId));
    }

    /**
     * GET /api/v1/buckets/{bucketId}/objects/{objectId}
     * Gets metadata for a specific object.
     * Response: 200 OK with object metadata
     */
    @GetMapping("/{objectId}")
    public ResponseEntity<ObjectResponseDto> getObjectMetadata(
            @PathVariable UUID bucketId,
            @PathVariable UUID objectId) {
        return ResponseEntity.ok(objectService.getObjectMetadata(bucketId, objectId));
    }

    /**
     * GET /api/v1/buckets/{bucketId}/objects/{objectId}/download
     * Downloads the actual file content.
     * Response: 200 OK with the file as an attachment (forces download dialog)
     */
    @GetMapping("/{objectId}/download")
    public ResponseEntity<Resource> downloadObject (
            @PathVariable UUID bucketId,
            @PathVariable UUID objectId) {

        // Get the object metadata first so we know the filename and content type
        ObjectResponseDto metadata = objectService.getObjectMetadata(bucketId, objectId);
        Resource file = objectService.downloadObject(bucketId, objectId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metadata.objectKey() + "\"")
                .body(file);
    }

    /**
     * DELETE /api/v1/buckets/{bucketId}/objects/{objectId}
     * Deletes an object (database record + file on disk).
     * Response: 204 No Content
     */
    @DeleteMapping("/{objectId}")
    public ResponseEntity<Void> deleteObject(
            @PathVariable UUID bucketId,
            @PathVariable UUID objectId) {
        objectService.deleteObject(bucketId, objectId);
        return ResponseEntity.noContent().build();
    }

}
