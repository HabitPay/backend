package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.MemberProfileResponse;
import com.habitpay.habitpay.global.config.aws.S3FileService;
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

        // TODO: 꼭 이미지를 presigned url 로 받아와야 할 필요가 있을까?
        String imageUrl = "";
        if (imageFileName.isEmpty() == false) {
            imageUrl = s3FileService.getGetPreSignedUrl("profiles", imageFileName);
        }
        return SuccessResponse.of("", MemberProfileResponse.of(member, imageUrl));
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
