package com.object_storage_service.object_storage_service_api.controller;

import com.object_storage_service.object_storage_service_api.domain.dto.BucketDto;
import com.object_storage_service.object_storage_service_api.domain.dto.CreateBucketRequest;
import com.object_storage_service.object_storage_service_api.domain.entity.Bucket;
import com.object_storage_service.object_storage_service_api.service.BucketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


/**
 * REST controller for bucket operations.
 *
 * All endpoints start with /api/v1/buckets (inherited from class-level @RequestMapping)
 *
 * @RestController = @Controller + @ResponseBody (returns JSON automatically)
 */
@RestController
@RequestMapping(path = "/api/v1/buckets")
public class BucketController {

    private final BucketService bucketService;

    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    /**
     * POST /api/v1/buckets
     * Request body: { "name": "my-bucket" }
     * Response: 201 Created with the bucket details
     */
    @PostMapping
    public ResponseEntity<BucketDto> createBucket(@Valid @RequestBody CreateBucketRequest request) {
        BucketDto bucket = bucketService.createBucket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bucket);
    }

    /**
     * GET /api/v1/buckets
     * Response: 200 OK with list of all buckets
     */
    @GetMapping
    public ResponseEntity<List<BucketDto>> listBuckets() {
        return ResponseEntity.ok(bucketService.listBuckets());
    }

    /**
     * GET /api/v1/buckets/{id}
     * Response: 200 OK with bucket details
     * Error: 404 if bucket not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<BucketDto> getBucket(@PathVariable UUID id) {
        return ResponseEntity.ok(bucketService.getBucket(id));
    }

    /**
     * DELETE /api/v1/buckets/{id}
     * Response: 204 No Content (success, no body)
     * Error: 404 if bucket not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBucket(@PathVariable UUID id) {
        bucketService.deleteBucket(id);
        return ResponseEntity.noContent().build();
    }
}
