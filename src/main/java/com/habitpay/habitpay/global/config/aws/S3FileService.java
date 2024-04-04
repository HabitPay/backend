package com.habitpay.habitpay.global.config.aws;

import io.awspring.cloud.s3.S3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3FileService {
    private final S3Operations amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String getPutPreSignedUrl(String prefix, String fileName) {
        String filePath = String.format("%s/%s", prefix, fileName);
        try (S3Presigner presigner = S3Presigner.create()) {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(2))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            log.info("[S3FileService] getPutPreSignedUrl: {}", presignedRequest.url().toString());
            return presignedRequest.url().toString();
        }
    }

    public String getGetPreSignedUrl(String prefix, String fileName) {
        String filePath = String.format("%s/%s", prefix, fileName);
        try (S3Presigner presigner = S3Presigner.create()) {
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
}
