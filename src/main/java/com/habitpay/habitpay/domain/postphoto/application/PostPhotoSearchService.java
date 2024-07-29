package com.habitpay.habitpay.domain.postphoto.application;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.dao.PostPhotoRepository;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import com.habitpay.habitpay.domain.postphoto.exception.PhotoNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostPhotoSearchService {

    private final PostPhotoRepository postPhotoRepository;

    public PostPhoto getPostPhotoById(Long id) {
        return postPhotoRepository.findById(id)
                .orElseThrow(() -> new PhotoNotFoundException(id));
    }

    public List<PostPhoto> findAllByPost(ChallengePost post) {
        return postPhotoRepository.findAllByChallengePost(post);
    }
}
