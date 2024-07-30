package com.habitpay.habitpay.domain.refreshtoken.application;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.dao.RefreshTokenRepository;
import com.habitpay.habitpay.domain.refreshtoken.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenDeleteService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void delete(Member member) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(member);
//        refreshTokenRepository.delete();
    }
}
