package com.habitpay.habitpay.domain.member.api;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberRequest;
import com.habitpay.habitpay.global.config.jwt.TokenProvider;
import com.habitpay.habitpay.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApi {
    private final MemberRepository repository;
    private final TokenProvider tokenProvider;

    @PostMapping("/member")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateMember(@RequestBody MemberRequest memberRequest, HttpServletRequest request) {
        String email = tokenProvider.getEmail(CookieUtil.getAccessToken(request));
        String nickname = memberRequest.getNickname();

        log.info("[POST /member] email: {}, nickname: {}", email, nickname);

        Optional<Member> optionalMember = Optional.ofNullable(repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        log.info("[POST /member] 회원 조회 성공");

        Member member = optionalMember.get();
        member.activate(nickname);
        repository.save(member);

        log.info("[POST /member] 회원 활성화 성공");
    }
}
