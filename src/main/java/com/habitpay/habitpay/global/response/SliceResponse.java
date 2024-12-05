package com.habitpay.habitpay.global.response;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public record SliceResponse<T>(
    List<T> content,
    int pageNumber,
    int size,
    boolean isLast,
    boolean isFirst,
    boolean isEmpty,
    boolean hasNextPage,
    Pageable pageable
) {

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(
            slice.getContent(),
            slice.getNumber() + 1,
            slice.getNumberOfElements(),
            slice.isLast(),
            slice.isFirst(),
            slice.isEmpty(),
            slice.hasNext(),
            slice.getPageable()
        );
    }
}
