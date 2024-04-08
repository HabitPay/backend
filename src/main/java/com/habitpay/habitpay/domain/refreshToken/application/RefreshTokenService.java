package com.habitpay.habitpay.domain.refreshToken.application;

import com.habitpay.habitpay.domain.refreshToken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshToken.domain.RefreshToken;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // todo: 예외 메시지 수정
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("이런 refresh token은 없다"));
    }
}
