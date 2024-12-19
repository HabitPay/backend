package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.postphoto.dto.ModifyPostPhotoData;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostPhotoUpdateService {

    private final PostPhotoUtilService postPhotoUtilService;

    public void changePhotoViewOrder(List<ModifyPostPhotoData> modifyPostPhotoDataList) {
        Optional.ofNullable(modifyPostPhotoDataList).ifPresent(list -> list.forEach(
            photo -> postPhotoUtilService.changeViewOrder(photo.getPhotoId(),
                photo.getViewOrder())));
    }
}