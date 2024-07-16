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
    private final MemberUpdateService memberUpdateService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    public SuccessResponse<MemberActivationResponse> activate(MemberActivationRequest memberActivationRequest, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        if (member.isActive()) {
            // TODO: 500 이 아닌 403 에러 반환하기
            throw new IllegalArgumentException("이미 활성된 사용자 입니다.");
        }

        String nickname = memberActivationRequest.getNickname();
        if (!memberUpdateService.isNicknameValidFormat(nickname)) {
            String message = ErrorResponse.INVALID_NICKNAME_RULE.getMessage();
            throw new CustomJwtException(HttpStatus.UNPROCESSABLE_ENTITY, CustomJwtErrorInfo.BAD_REQUEST, message);
        }

        member.activate(nickname);
        memberRepository.save(member);

        // TODO: 토큰 생성 여부 논의 필요
        String accessToken = tokenService.createAccessToken(member.getId());
        Long expiresIn = tokenService.getAccessTokenExpiresInToMillis();
        return SuccessResponse.of(
                "회원가입이 완료되었습니다.",
                MemberActivationResponse.of(member, accessToken, expiresIn, "Bearer"));
    }
}