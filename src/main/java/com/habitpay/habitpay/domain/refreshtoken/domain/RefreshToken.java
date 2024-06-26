package com.habitpay.habitpay.domain.refreshtoken.domain;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String refreshToken;

    // todo: 과정 좀 복잡하면 즉시 삭제
    @Column(nullable = false)
    private String loginIp;

    public RefreshToken(Member member, String refreshToken, String loginIp) {
        this.member = member;
        this.refreshToken = refreshToken;
        this.loginIp = loginIp;
    }

    public RefreshToken update(String newRefreshToken, String newLoginIp) {
        this.refreshToken = newRefreshToken;
        this.loginIp = newLoginIp;

        return this;
    }
}
