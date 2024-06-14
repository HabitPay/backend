package com.habitpay.habitpay.domain.challengeparticipationrecord.domain;

import com.habitpay.habitpay.domain.challengepost.domain.ChallengePost;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "challenge_participation_record")
public class ChallengeParticipationRecord extends BaseTime {
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

//    @Builder
//    public ChallengeParticipationRecord() {}
}
