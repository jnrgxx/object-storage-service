package com.object_storage_service.object_storage_service_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler that catches exceptions thrown by controllers
 * and returns structured JSON error responses instead of raw stack traces.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * It intercepts ALL exceptions from ALL controllers in the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles when a bucket is not found (e.g., GET /api/v1/buckets/{invalid-id})
     * Returns HTTP 404 with a descriptive message.
     */
    @ExceptionHandler(BucketNotFoundException.class)
    public ProblemDetail handleBucketNotFound(BucketNotFoundException ex) {
        // ProblemDetail is a standardized error response format (RFC 7807)
        return createProblemDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles when an object is not found (e.g., GET /objects/{invalid-id})
     * Returns HTTP 404.
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    public ProblemDetail handleObjectNotFound(ObjectNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles validation errors from @Valid annotations on request bodies.
     * For example, if someone sends a bucket name that's empty or too short,
     * Spring throws MethodArgumentNotValidException automatically.
     * Returns HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        var detail = createProblemDetail(HttpStatus.BAD_REQUEST, "Validation failed");

        // Extract the first validation error message to show the user
        // e.g., "Bucket name must be between 3 and 63 characters"
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        detail.setProperty("error", errorMessage);
        return detail;
    }

    /**
     * Catch-all for any unhandled RuntimeException (500 Internal Server Error).
     * This is a safety net — you should handle specific exceptions where possible.
     */
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleGenericError(RuntimeException ex) {
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /**
     * Helper method to create a consistent ProblemDetail response.
     * ProblemDetail gives us a standardized JSON structure:
     * {
     *   "type": "about:blank",
     *   "title": "Not Found",
     *   "status": 404,
     *   "detail": "Bucket not found with id: ...",
     *   "instance": "/api/v1/buckets/..."
     *   "timestamp": "2026-06-10T23:53:00"
     * }
     */
    private ProblemDetail createProblemDetail(HttpStatus status, String message) {
        var detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setTitle(status.getReasonPhrase());
        detail.setProperty("timestamp", LocalDateTime.now().toString());
        return detail;
    }
}
