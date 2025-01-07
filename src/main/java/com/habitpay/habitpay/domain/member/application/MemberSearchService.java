package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberProfileResponse;
import com.habitpay.habitpay.domain.member.exception.MemberNotFoundException;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberSearchService {
    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;

    public SuccessResponse<MemberProfileResponse> getMemberProfile(Member member) {
        String imageFileName = Optional.ofNullable(member.getImageFileName()).orElse("");
        String imageUrl = imageFileName.isEmpty() ? "" : s3FileService.getGetPreSignedUrl("profiles", imageFileName);
        
        return SuccessResponse.of(SuccessCode.NO_MESSAGE, MemberProfileResponse.of(member, imageUrl));
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }

    public String getMemberProfileImageUrl(String imageFileName) {
        return Optional.ofNullable(imageFileName)
                .filter(fileName -> !fileName.isEmpty())
                .map(fileName -> s3FileService.getGetPreSignedUrl("profiles", fileName))
                .orElse("");
    }
}
