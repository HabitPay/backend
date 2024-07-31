package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.refreshtoken.application.RefreshTokenDeleteService;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class MemberDeleteService {

    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;
    private final RefreshTokenDeleteService refreshTokenDeleteService;

    @Transactional
    public SuccessResponse<Long> delete(Member member) {
        log.info("[DELETE /member] imageFileName: {}", member.getImageFileName());
        Optional.ofNullable(member.getImageFileName())
                .ifPresent((imageFileName) -> s3FileService.deleteImage("profiles", imageFileName));

        refreshTokenDeleteService.delete(member);

        member.clear();
        memberRepository.save(member);

        return SuccessResponse.of(SuccessCode.DELETE_MEMBER_ACCOUNT_SUCCESS, member.getId());
    }

}

