package com.habitpay.habitpay.global.util;

import com.habitpay.habitpay.domain.model.Image;

public class ImageUtil {
    public static final long MB = 1024 * 1024;
    public static final long PROFILE_IMAGE_SIZE_LIMIT = 1 * MB;

    public static boolean isValidImageExtension(String extension) {
        return Image.VALID_EXTENSION.contains(extension.toUpperCase());
    }

    public static boolean isValidFileSize(Long size) {
        return 0 < size && size <= PROFILE_IMAGE_SIZE_LIMIT;
    }
}
