package com.habitpay.habitpay.domain.proof.domain;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "proof")
public class Proof {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // todo : enrollment 도메인 적용하기
//    @ManyToOne
//    @JoinColumn
//    private ChallengeEnrollment enrollment;

    @OneToOne
    @JoinColumn
    private ChallengePost post;

    @CreatedDate
    private LocalDateTime createdAt;

//    @Builder
//    public Proof() {}
}
