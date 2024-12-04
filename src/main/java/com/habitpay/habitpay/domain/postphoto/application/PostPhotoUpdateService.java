package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.postphoto.dto.ModifyPostPhotoData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostPhotoUpdateService {

    private final PostPhotoUtilService postPhotoUtilService;

    public void changePhotoViewOrder(List<ModifyPostPhotoData> modifyPostPhotoDataList) {
        Optional.ofNullable(modifyPostPhotoDataList).orElse(Collections.emptyList())
                .forEach(photo -> postPhotoUtilService.changeViewOrder(photo.getPhotoId(), photo.getViewOrder()));
    }
}