package com.object_storage_service.object_storage_service_api.exception;

public class BucketNotFoundException extends RuntimeException {
    public BucketNotFoundException(String message) {
        super(message);
    }
}
