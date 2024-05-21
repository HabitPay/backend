package com.habitpay.habitpay.domain.postPhoto.dao;

import com.habitpay.habitpay.domain.postPhoto.domain.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostPhotoRepository extends JpaRepository<PostPhoto, Long> {
    Optional<PostPhoto> findByPostId(Long postId);
}
