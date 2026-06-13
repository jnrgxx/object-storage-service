package com.object_storage_service.object_storage_service_api.service.impl;

import com.object_storage_service.object_storage_service_api.config.StorageConfig;
import com.object_storage_service.object_storage_service_api.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Implementation of StorageService that saves files to the local filesystem.
 *
 * File structure on disk:
 * ./uploads/
 *   ├── my-bucket/
 *   │   ├── uuid1-photo.jpg
 *   │   └── uuid2-document.pdf
 *   └── another-bucket/
 *       └── uuid3-file.txt
 *
 * Files are prefixed with UUIDs to prevent name collisions
 * (two files with the same name can exist in the same bucket).
 */
@Service
public class StorageServiceImpl implements StorageService {


    private final Path uploadDir;

    /**
     * Spring injects StorageConfig here (constructor injection).
     * This is the recommended way to inject dependencies in Spring.
     */
    public StorageServiceImpl(StorageConfig storageConfig) {
        this.uploadDir = storageConfig.getUploadDir();
    }

    @Override
    public String store(String bucketName, String objectKey, InputStream data) {
        try {
            // Create the bucket directory if it doesn't exist
            // e.g., ./uploads/my-bucket/
            Path bucketDir = uploadDir.resolve(bucketName);
            Files.createDirectories(bucketDir);

            // Generate a unique filename to prevent overwrites
            // Format: {uuid}-{original-filename}
            // e.g., "550e8400-e29b-41d4-a716-446655440000-photo.jpg"
            String uniqueFileName = java.util.UUID.randomUUID() + "-" + objectKey;
            Path targetPath = bucketDir.resolve(uniqueFileName);

            // Copy the uploaded file to the target path
            // StandardCopyOption.REPLACE_EXISTING means if somehow a file
            // with the same name exists, overwrite it
            Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toAbsolutePath().toString();


        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + objectKey, e);
        }
    }

    @Override
    public Resource load(String storagePath) {
        // Convert the stored path string back to a Path object
        try {
            Path filepath = Path.of(storagePath);

            // UrlResource wraps the file so Spring can stream it to the client
            // with proper content-type detection

            Resource resource = new UrlResource(filepath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + storagePath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + storagePath, e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Path filePath = Path.of(storagePath);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + storagePath, e);
        }
    }

    @Override
    public void deleteBucket(String bucketName) {
        try {
            Path bucketDir = uploadDir.resolve(bucketName);
            if (Files.exists(bucketDir)) {
                // deleteDirectory recursively deletes the folder and all files inside
                // This is a Java 21 feature (Files.walk + sorted reverse)
                try (var files = Files.walk(bucketDir)) {
                    files.sorted((a, b) ->  b.compareTo(a))
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (Exception e) {
                                    throw new RuntimeException("Failed to delete: " + path, e);
                                }
                            });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bucket: " + bucketName, e);
        }
    }
}