package com.habitpay.habitpay.domain.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Image {
    VALID_EXTENSION("JPG,JPEG,PNG");

    private Set<String> imageExtensionSet;

    Image(String extensionString) {
        this.imageExtensionSet = Arrays.stream(extensionString.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public boolean contains(String extension) {
        return this.imageExtensionSet.contains(extension);
    }

}
