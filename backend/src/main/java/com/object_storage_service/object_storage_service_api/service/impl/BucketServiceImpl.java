package com.object_storage_service.object_storage_service_api.service.impl;

import com.object_storage_service.object_storage_service_api.domain.dto.BucketDto;
import com.object_storage_service.object_storage_service_api.domain.dto.CreateBucketRequest;
import com.object_storage_service.object_storage_service_api.domain.entity.Bucket;
import com.object_storage_service.object_storage_service_api.exception.BucketNotFoundException;
import com.object_storage_service.object_storage_service_api.mapper.BucketMapper;
import com.object_storage_service.object_storage_service_api.repository.BucketRepository;
import com.object_storage_service.object_storage_service_api.repository.ObjectRepository;
import com.object_storage_service.object_storage_service_api.service.BucketService;
import com.object_storage_service.object_storage_service_api.service.StorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of BucketService.
 *
 * @Transactional means each method runs inside a database transaction.
 * If something fails, all changes are rolled back automatically.
 */
@Service
@Transactional
public class BucketServiceImpl implements BucketService {
    private final BucketRepository bucketRepository;
    private final ObjectRepository objectRepository;
    private final BucketMapper bucketMapper;
    private final StorageService storageService;

    // Constructor injection — Spring provides all dependencies automatically
    public BucketServiceImpl(BucketRepository bucketRepository,
                             ObjectRepository objectRepository,
                             BucketMapper bucketMapper,
                             StorageService storageService) {
        this.bucketRepository = bucketRepository;
        this.objectRepository = objectRepository;
        this.bucketMapper = bucketMapper;
        this.storageService = storageService;
    }

    @Override
    public BucketDto createBucket(CreateBucketRequest request) {
        // Check if bucket name already exists (S3 buckets must have unique names)
        if (bucketRepository.existsByName(request.name())) {
                throw new IllegalArgumentException(
                        "Bucket with name '" + request.name() + "' already exists"
                );
        }

        // Convert request to entity and save to database
        Bucket bucket = bucketMapper.toEntity(request);
        bucket = bucketRepository.save(bucket);

        // Return DTO - ObjectCount is 0 since it's a new bucket
        return bucketMapper.toDto(bucket, 0);
    }

    @Override
    public List<BucketDto> listBuckets() {
        // Get all buckets from database
        List<Bucket> buckets = bucketRepository.findAll();

        // Convert each bucket to DTO with its object count
        return buckets.stream()
                .map(bucket -> {
                    long count = objectRepository.countByBucketId(bucket.getId());
                    return bucketMapper.toDto(bucket, count);
                })
                .toList();
    }

    @Override
    public BucketDto getBucket(UUID id) {
        // Find bucket or throw 404
        Bucket bucket = bucketRepository.findById(id)
                .orElseThrow(() -> new BucketNotFoundException(
                    "Bucket not found with id: " + id
                ));
        long count = objectRepository.countByBucketId(id);
        return bucketMapper.toDto(bucket, count);
    }

    @Override
    public void deleteBucket(UUID id) {
        // find bucket or throw 404
        Bucket bucket = bucketRepository.findById(id)
                .orElseThrow(() -> new BucketNotFoundException(
                    "Bucket not found with id: " + id
                ));

        // Delete all files from disk first
        storageService.deleteBucket(bucket.getName());

        // Delete all object records from database
        objectRepository.deleteAllByBucketId(id);

        // Delete the bucket record from database
        bucketRepository.delete(bucket);
    }

}
