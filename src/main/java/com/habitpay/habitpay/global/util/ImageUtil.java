package com.habitpay.habitpay.global.util;

import com.habitpay.habitpay.domain.model.Image;

public class ImageUtil {
    public static boolean isValidImageExtension(String extension) {
        return Image.VALID_EXTENSION.contains(extension.toUpperCase());
    }
}
