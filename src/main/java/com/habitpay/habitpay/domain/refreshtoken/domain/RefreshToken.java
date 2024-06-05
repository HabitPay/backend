package com.habitpay.habitpay.domain.refreshtoken.domain;

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

    // todo: 외래키 연결
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    // todo: 과정 좀 복잡하면 즉시 삭제
    @Column(name = "login_ip", nullable = false)
    private String loginIp;

    public RefreshToken(Long userId, String refreshToken, String loginIp) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.loginIp = loginIp;
    }

    public RefreshToken update(String newRefreshToken, String newLoginIp) {
        this.refreshToken = newRefreshToken;
        this.loginIp = newLoginIp;

        return this;
    }
}
