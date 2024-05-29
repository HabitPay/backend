package com.habitpay.habitpay.global.config.aws;

import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3FileService {
    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public void deleteImage(String prefix, String fileName) {
        String filePath = String.format("%s/%s", prefix, fileName);
        try {
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();
            s3Client.deleteObject(objectRequest);
            log.info("[S3FileService] deleteObject: {}", filePath);

        } catch (S3Exception exception) {
            throw new S3Exception("Failed to delete s3 object", exception);
        }
    }

    public String getPutPreSignedUrl(String prefix, String fileName, String extension, Long contentLength) {
        String filePath = String.format("%s/%s", prefix, fileName);
        String contentType = String.format("image/%s", extension);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        log.info("[S3FileService] getPutPreSignedUrl: {}", presignedRequest.url().toString());
        return presignedRequest.url().toString();
    }

    public String getGetPreSignedUrl(String prefix, String fileName) {
        String filePath = String.format("%s/%s", prefix, fileName);
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2))
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        log.info("[S3FileService] getGetPreSignedUrl: {}", presignedRequest.url().toString());
        return presignedRequest.url().toExternalForm();
    }
}
