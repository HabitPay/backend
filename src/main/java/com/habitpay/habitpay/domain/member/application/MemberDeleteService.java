package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.exception.MemberNotFoundException;
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
        Member member = memberRepository.findById(id)
                .filter(Member::isActive)
                .orElseThrow(() -> new MemberNotFoundException(id));

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

