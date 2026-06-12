package com.object_storage_service.object_storage_service_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

/**
 * Configuration class that reads the 'storage.upload-dir' property
 * from application.properties and makes it available as a typed Java object.
 *
 * @ConfigurationProperties binds properties with the 'storage' prefix
 * to this class's fields automatically.
 *
 * Property in application.properties:  storage.upload-dir=./uploads
 * Becomes:                              storageConfig.getUploadDir() = Path("./uploads")
 */

@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageConfig {

    /**
     * The root directory where all uploaded files will be stored.
     * Defaults to "./uploads" which means a folder called 'uploads'
     * in your project root directory.
     *
     * Path is used instead of String because it provides useful methods
     * like resolve() to build subpaths: uploadDir.resolve("bucket-name/file.txt")
     */
    private Path uploadDir = Path.of("./uploads");

    public Path getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(Path uploadDir) {
        this.uploadDir = uploadDir;
    }
}
