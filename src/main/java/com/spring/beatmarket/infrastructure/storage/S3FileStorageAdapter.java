package com.spring.beatmarket.infrastructure.storage;

import com.spring.beatmarket.domain.catalog.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Override
    public String upload(final byte[] content, final String fileKey) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build(),
                RequestBody.fromBytes(content));

        return fileKey;
    }

    @Override
    public void delete(final String fileKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
