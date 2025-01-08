package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberDetailsResponse;
import com.habitpay.habitpay.domain.member.dto.MemberProfileResponse;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberDetailsService {
    private final MemberSearchService memberSearchService;
    private final S3FileService s3FileService;

    public SuccessResponse<MemberDetailsResponse> getMemberDetails(Long memberId, Member currentUser) {
        Member member = memberSearchService.getMemberById(memberId);
        Boolean isCurrentUser = currentUser.equals(member);

        String imageFileName = Optional.ofNullable(member.getImageFileName()).orElse("");
        String imageUrl = imageFileName.isEmpty() ? "" : s3FileService.getGetPreSignedUrl("profiles", imageFileName);

        return SuccessResponse.of(SuccessCode.NO_MESSAGE, MemberDetailsResponse.of(member, imageUrl, isCurrentUser));
    }
}
