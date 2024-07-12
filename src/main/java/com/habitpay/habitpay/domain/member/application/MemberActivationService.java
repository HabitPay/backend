package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberActivationRequest;
import com.habitpay.habitpay.domain.member.dto.MemberActivationResponse;
import com.habitpay.habitpay.domain.refreshtoken.exception.CustomJwtException;
import com.habitpay.habitpay.global.config.jwt.TokenService;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberActivationService {
    private final MemberProfileService memberProfileService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    public SuccessResponse<MemberActivationResponse> activate(MemberActivationRequest memberActivationRequest, Long id) {
        String nickname = memberActivationRequest.getNickname();

        // TODO: 예외처리 구현하기
        if (!memberProfileService.isNicknameValidFormat(nickname)) {
            String message = ErrorResponse.INVALID_NICKNAME_RULE.getMessage();
            throw new CustomJwtException(HttpStatus.UNPROCESSABLE_ENTITY, CustomJwtErrorInfo.BAD_REQUEST, message);
        }

        Member member = memberRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        member.activate(nickname);
        memberRepository.save(member);
        String accessToken = tokenService.createAccessToken(member.getId());
        Long expiresIn = tokenService.getAccessTokenExpiresInToMillis();
        return SuccessResponse.of(
                "회원가입이 완료되었습니다.",
                MemberActivationResponse.of(member, accessToken, expiresIn, "Bearer"));
    }
}