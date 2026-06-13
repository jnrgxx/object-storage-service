package com.object_storage_service.object_storage_service_api.service.impl;

import com.object_storage_service.object_storage_service_api.domain.dto.ObjectResponseDto;
import com.object_storage_service.object_storage_service_api.domain.entity.Bucket;
import com.object_storage_service.object_storage_service_api.domain.entity.StorageObject;
import com.object_storage_service.object_storage_service_api.exception.BucketNotFoundException;
import com.object_storage_service.object_storage_service_api.exception.ObjectNotFoundException;
import com.object_storage_service.object_storage_service_api.mapper.ObjectMapper;
import com.object_storage_service.object_storage_service_api.repository.BucketRepository;
import com.object_storage_service.object_storage_service_api.repository.ObjectRepository;
import com.object_storage_service.object_storage_service_api.service.ObjectService;
import com.object_storage_service.object_storage_service_api.service.StorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ObjectServiceImpl implements ObjectService {
    private final BucketRepository bucketRepository;
    private final ObjectRepository objectRepository;
    private final ObjectMapper objectMapper;
    private final StorageService storageService;

    public ObjectServiceImpl(BucketRepository bucketRepository,
                             ObjectRepository objectRepository,
                             ObjectMapper objectMapper,
                             StorageService storageService) {
        this.bucketRepository = bucketRepository;
        this.objectRepository = objectRepository;
        this.objectMapper = objectMapper;
        this.storageService = storageService;
    }

    @Override
    public ObjectResponseDto uploadObject(UUID bucketId, MultipartFile file) {
        // 1. Find the bucket or throw 404
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException(
                    "Bucket not found with id: " + bucketId
                ));

        try {
            // 2. Save the file to disk using StorageService
            String storagePath = storageService.store(
                    bucket.getName(),
                    file.getOriginalFilename(),
                    file.getInputStream()
            );

            // 3. Create entity and save metadata to database
            StorageObject storageObject = StorageObject.builder()
                    .bucket(bucket)
                    .objectKey(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .storagePath(storagePath)
                    .build();

            storageObject = objectRepository.save(storageObject);

            // 4. Return DTO to the client
            return objectMapper.toDto(storageObject);
        } catch (Exception e) {
            throw new RuntimeException("Failed to uplaod file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public List<ObjectResponseDto> listObjects(UUID bucketId) {
        // Verify bucket exists first
        if (!bucketRepository.existsById(bucketId)) {
            throw new BucketNotFoundException("Bucket not found with id: " + bucketId);
        }

        // Find all objects in this bucket and convert to DTOs
        return objectRepository.findByBucketId(bucketId)
                .stream()
                .map(objectMapper::toDto)
                .toList();

    }

    @Override
    public ObjectResponseDto getObjectMetadata(UUID bucketId, UUID objectId) {
        // Find the object and verify it belong to this bucket
        StorageObject object = findObjectInBucket(bucketId, objectId);
        return objectMapper.toDto(object);
    }

    @Override
    public Resource downloadObject(UUID bucketId, UUID objectId) {
        // Find the object and load its file from disk
        StorageObject object = findObjectInBucket(bucketId, objectId);
        return storageService.load(object.getStoragePath());
    }

    @Override
    public void deleteObject(UUID bucketId, UUID objectId) {
        // Find the Object
        StorageObject object = findObjectInBucket(bucketId, objectId);

        // Delete file form disk
        storageService.delete(object.getStoragePath());

        // Delete metadata from database
        objectRepository.delete(object);
    }

    /**
     * Helper method that finds an object and verifies it belongs to the given bucket.
     * This prevents someone from accessing objects in a bucket they shouldn't have access to.
     */
    private StorageObject findObjectInBucket(UUID bucketId, UUID objectId) {
        StorageObject object = objectRepository.findById(objectId)
                .orElseThrow(() -> new ObjectNotFoundException(
                    "Object not found with id: " + objectId
                ));

        // Security check: does this object actually belong to the specified bucket?
        if (!object.getBucket().getId().equals(bucketId)) {
            throw new ObjectNotFoundException(
                "Object with id " + objectId + " not found in bucket " + bucketId
            );
        }

        return object;
    }
}
