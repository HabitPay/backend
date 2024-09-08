package com.habitpay.habitpay.domain.postphoto.domain;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "post_photo")
public class PostPhoto extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_post_id")
    private ChallengePost challengePost;

    @Column
    private String imageFileName;

    @Column(nullable = false)
    private Long viewOrder;

    @Builder
    public PostPhoto(ChallengePost post, String imageFileName, Long viewOrder) {
        this.challengePost = post;
        this.imageFileName = imageFileName;
        this.viewOrder = viewOrder;
    }

    public void changeViewOrder(Long newOrder) {
        this.viewOrder = newOrder;
    }

}
