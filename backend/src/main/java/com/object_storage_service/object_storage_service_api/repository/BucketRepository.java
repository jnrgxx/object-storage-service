package com.object_storage_service.object_storage_service_api.repository;

import com.object_storage_service.object_storage_service_api.domain.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BucketRepository extends JpaRepository<Bucket, UUID> {
    Optional<Bucket> findByName(String name); // Spring Data JPA automatically implements this based on the method name — it generates `SELECT * FROM buckets WHERE name = ?`

    boolean existsByName(String name); // Same pattern, returns true/false

}
