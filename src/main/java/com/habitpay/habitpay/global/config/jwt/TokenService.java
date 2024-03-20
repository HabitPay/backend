package com.habitpay.habitpay.global.config.jwt;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public String createAccessToken(String email) {
        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        Member member = optionalMember.get();

        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }
}
