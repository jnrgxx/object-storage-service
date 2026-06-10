package com.object_storage_service.object_storage_service_api.repository;

import com.object_storage_service.object_storage_service_api.domain.entity.StorageObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ObjectRepository extends JpaRepository<StorageObject, UUID> {
    // Finds all objects in a bucket. Spring Data JPA understands `bucketId` refers to the `bucket.id` field in the `StorageObject` entity
    List<StorageObject> findByBucketId(UUID bucketId);

    // Returns the count of objects in a bucket (used for `objectCount` in BucketDto)
    long countByBucketId(UUID bucketId);

    // Deletes all objects in a bucket (used when deleting a bucket)
    void deleteAllByBucketId(UUID bucketId);
}
