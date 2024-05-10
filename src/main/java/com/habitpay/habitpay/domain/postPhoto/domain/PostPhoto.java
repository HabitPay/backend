package com.habitpay.habitpay.domain.postPhoto.domain;

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

    // todo : challengePost와 외래키 연결
    @Column(nullable = false)
    private Long postId;

    @Column
    private String url;

    @Column(nullable = false)
    private Long viewOrder;

    @Builder
    public PostPhoto(Long postId, String url, Long viewOrder) {
        this.postId = postId;
        this.url = url;
        this.viewOrder = viewOrder;
    }

    public void changeViewOrder(Long newOrder) {
        // todo : 두 사진 객체의 순서가 바뀌는 게 보장되어야 함.
        // todo : 순서를 숫자로 받지 않고, postPhoto 객체를 받아서 서로 viewOrder 값을 교환하는 방식도 고려
        this.viewOrder = newOrder;
    }

//    public Member getUploader() {
//        // todo : postId -> challengePost.getWriter()로 작성자 찾기
//    }
}
