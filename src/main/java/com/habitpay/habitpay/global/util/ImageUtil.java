package com.habitpay.habitpay.global.util;

import com.habitpay.habitpay.domain.model.Image;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
public class ImageUtil {

    @Value("${app.max-upload-file-size}")
    private DataSize maxUploadSize;

    public void validateImageFormat(Long contentLength, String extension) {
        // 1. 이미지 크기 제한이 넘을 경우
        if (isValidFileSize(contentLength)) {
            throw new InvalidValueException(
                String.format("size: %dMB", contentLength / 1024 / 1024),
                ErrorCode.PROFILE_IMAGE_SIZE_TOO_LARGE);
        }

        // 2. 이미지 확장자가 허용되지 않은 경우
        if (isValidImageExtension(extension)) {
            throw new InvalidValueException(String.format("extension: %s", extension),
                ErrorCode.UNSUPPORTED_IMAGE_EXTENSION);
        }
    }

    public boolean isValidImageExtension(String extension) {
        return Image.VALID_EXTENSION.contains(extension.toUpperCase());
    }

    public boolean isValidFileSize(Long size) {
        return 0 < size && size <= maxUploadSize.toBytes();
    }
}
