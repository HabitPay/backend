package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberDeleteService {

    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;

    public SuccessResponse<Long> delete(Long id) {
        // TODO: 공통 예외처리 응답으로 처리하기
        Member member = memberRepository.findById(id)
                .filter(Member::isActive)
                .orElseThrow(() -> new IllegalAccessError("이미 탈퇴한 사용자입니다."));

        String imageFileName = member.getImageFileName();
        log.info("[DELETE /member] imageFileName: {}", imageFileName);
        if (imageFileName != null) {
            s3FileService.deleteImage("profiles", imageFileName);
        }

        member.clear();
        memberRepository.save(member);

        return SuccessResponse.of("정상적으로 탈퇴되었습니다.", id);
    }

}
