package com.habitpay.habitpay.domain.postphoto.domain;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.member.domain.Member;
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

    @ManyToOne
    @JoinColumn(name = "challenge_post_id")
    private ChallengePost post;

    @Column
    private String imageFileName;

    @Column(nullable = false)
    private Long viewOrder;

    @Builder
    public PostPhoto(ChallengePost post, String imageFileName, Long viewOrder) {
        this.post = post;
        this.imageFileName = imageFileName;
        this.viewOrder = viewOrder;
    }

    public void changeViewOrder(Long newOrder) {
        // todo : 두 사진 객체의 순서가 바뀌는 게 보장되어야 함. -> 혹시 겹치면 순서 앞선 것부터 나열한다거나,,
        this.viewOrder = newOrder;
    }

}
