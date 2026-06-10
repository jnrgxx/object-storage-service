package com.object_storage_service.object_storage_service_api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBucketRequest(
        @NotBlank(message = "Bucket name is required")
        @Size(min = 3, max = 63, message = "Bucket name must be between 3 and 63 characters")
        String name
) {

}
