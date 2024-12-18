package com.habitpay.habitpay.global.util;

import com.habitpay.habitpay.domain.model.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
public class ImageUtil {

    @Value("${app.max-upload-file-size}")
    private DataSize maxUploadSize;

    public boolean isValidImageExtension(String extension) {
        return Image.VALID_EXTENSION.contains(extension.toUpperCase());
    }

    public boolean isValidFileSize(Long size) {
        System.out.printf("maxUploadSize: %d\n", maxUploadSize.toBytes());
        return 0 < size && size <= maxUploadSize.toBytes();
    }
}
