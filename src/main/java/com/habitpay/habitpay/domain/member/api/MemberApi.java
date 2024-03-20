package com.habitpay.habitpay.domain.member.api;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
public class MemberApi {
    private final MemberRepository repository;

    MemberApi(MemberRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/member")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateMember(@RequestBody MemberRequest memberRequest) {
        String email = memberRequest.getEmail();
        String nickname = memberRequest.getNickname();

        log.info("email: {}, nickname: {}", email, nickname);

        Optional<Member> optionalMember = Optional.ofNullable(repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        log.info("회원 조회 성공");

        Member member = optionalMember.get();
        member.activate(nickname);
        repository.save(member);

        log.info("회원 정보 변경 성공");
    }
}
