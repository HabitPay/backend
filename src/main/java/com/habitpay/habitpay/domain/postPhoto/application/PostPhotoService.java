package com.habitpay.habitpay.domain.postPhoto.application;

import com.habitpay.habitpay.domain.postPhoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoService {

    private final PostPhotoRepository postPhotoRepository;

    // todo : S3FileService? 이미지 받아서 aws에 저장하고 url만 db에 저장
//    public PostPhoto save() {}

    public PostPhoto findById(Long id) {
        return postPhotoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));
    }

    // todo : post 별로 찾아주는 메서드 (진행 중)
    public List<PostPhoto> findAllByPost(Long postId) {
        return postPhotoRepository.findAll();
    }

    public void delete(Long id) {
        PostPhoto postPhoto = postPhotoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("(for debugging) not found : " + id));

        authorizePhotoUploader(postPhoto);
        // todo : aws에서도 이미지 파일 삭제하기
        postPhotoRepository.delete(postPhoto);
    }

    // todo : 사진 순서 데이터 어떻게 오는지 확인하고 작성하기
//    @Transactional
//    public PostPhoto changeViewOrder() {}

    private static void authorizePhotoUploader(PostPhoto postPhoto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("email : " + email);
//        if (!postPhoto.getUploader().getEmail().equals(email)) {
//            throw new IllegalArgumentException("(for debug) not authorized");
//        }
    }
}
