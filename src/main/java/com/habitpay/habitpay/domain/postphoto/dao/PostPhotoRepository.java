package com.habitpay.habitpay.domain.postphoto.dao;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.postphoto.domain.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostPhotoRepository extends JpaRepository<PostPhoto, Long> {
    Optional<List<PostPhoto>> findAllByPost(ChallengePost post);
}
