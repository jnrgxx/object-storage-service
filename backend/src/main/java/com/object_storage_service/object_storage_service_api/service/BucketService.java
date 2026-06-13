package com.object_storage_service.object_storage_service_api.service;

import com.object_storage_service.object_storage_service_api.domain.dto.BucketDto;
import com.object_storage_service.object_storage_service_api.domain.dto.CreateBucketRequest;

import java.util.List;
import java.util.UUID;

/**
 * Business logic for managing buckets.
 * A bucket is a container that holds objects (files).
 */
public interface BucketService {
    /**
     * Creates a new bucket with the given name.
     * Throws an exception if the name already exists.
     */
    BucketDto createBucket(CreateBucketRequest request);


    /**
     * Returns all buckets with their object counts.
     */
    List<BucketDto> listBuckets();

    /**
     * Returns a single bucket by its ID.
     * Throws BucketNotFoundException if not found.
     */
    BucketDto getBucket(UUID id);

    /**
     * Deletes a bucket, all its objects from the database,
     * and all its files from disk.
     */
    void deleteBucket(UUID id);
}
