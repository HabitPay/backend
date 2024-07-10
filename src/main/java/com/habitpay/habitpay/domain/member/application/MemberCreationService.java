package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberCreationRequest;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberCreationService {
    private final MemberProfileService memberProfileService;
    private final MemberRepository memberRepository;

    public void activate(MemberCreationRequest memberCreationRequest, Long id) {
        String nickname = memberCreationRequest.getNickname();
        if (!memberProfileService.isNicknameValidFormat(nickname)) {
            String message = ErrorResponse.INVALID_NICKNAME_RULE.getMessage();
            throw new CustomJwtException(HttpStatus.UNPROCESSABLE_ENTITY, CustomJwtErrorInfo.BAD_REQUEST, message);
        }

        Member member = memberRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        member.activate(nickname);
        memberRepository.save(member);
    }
}