# Object Storage Service

> 🚧 Work in Progress

An Amazon S3-inspired object storage platform supporting 
buckets, file uploads, metadata management, and 
secure file access.

## Tech Stack
- Java 21 + Spring Boot 3.5
- PostgreSQL
- React + TypeScript + TailwindCSS
- Docker

```angular2html
com.storageapi/
├── controller/
│   ├── BucketController.java
│   └── ObjectController.java
├── service/
│   ├── BucketService.java        ← interface
│   ├── ObjectService.java        ← interface
│   ├── StorageService.java       ← interface (NEW)
│   └── impl/
│       ├── BucketServiceImpl.java
│       ├── ObjectServiceImpl.java
│       └── StorageServiceImpl.java ← handles disk I/O (NEW)
├── repository/
│   ├── BucketRepository.java
│   └── ObjectRepository.java
├── domain/
│   ├── entity/
│   │   ├── Bucket.java
│   │   └── StorageObject.java
│   └── dto/
│       ├── BucketDto.java
│       ├── CreateBucketRequest.java
│       ├── UploadObjectRequest.java
│       └── ObjectResponseDto.java
├── mapper/
│   ├── BucketMapper.java
│   └── ObjectMapper.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── BucketNotFoundException.java
│   └── ObjectNotFoundException.java
└── config/
    └── StorageConfig.java        ← defines upload folder path (NEW)
```


## Running Locally
\`\`\`bash
docker-compose up
\`\`\`
Backend: http://localhost:8081
Swagger UI: http://localhost:8081/swagger-ui.html