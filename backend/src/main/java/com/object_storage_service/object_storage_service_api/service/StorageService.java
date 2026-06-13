package com.object_storage_service.object_storage_service_api.service;

import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * Handles the actual file storage on disk.
 * This layer separates database metadata from filesystem operations.
 *
 * Think of this as the "hard drive" layer:
 * - store() writes a file to disk, returns the path where it was saved
 * - load() reads a file from disk by its path
 * - delete() removes a file from disk by its path
 */
public interface StorageService {

    /**
     * Saves a file to disk inside a bucket-specific folder.
     *
     * @param bucketName the name of the bucket (creates a folder with this name)
     * @param objectKey  the original filename (e.g., "photo.jpg")
     * @param data       the file content as an InputStream
     * @return the full storage path (saved to DB for later retrieval)
     */
    String store(String bucketName, String objectKey, InputStream data);

    /**
     * Loads a file from disk as a downloadable Resource.
     *
     * @param storagePath the path returned by store()
     * @return a Resource that Spring can stream to the HTTP response
     */
    Resource load(String storagePath);

    /**
     * Deletes a file from disk.
     *
     * @param storagePath the path returned by store()
     */
    void delete(String storagePath);


    /**
     * Deletes an entire bucket folder and all files inside it.
     * Used when a bucket is deleted.
     *
     * @param bucketName the bucket folder to delete
     */
    void deleteBucket(String bucketName);
}
