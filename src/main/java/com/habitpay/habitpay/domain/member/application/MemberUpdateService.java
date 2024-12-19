package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateRequest;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateResponse;
import com.habitpay.habitpay.domain.member.dto.NicknameDto;
import com.habitpay.habitpay.domain.member.exception.InvalidNicknameException;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.util.ImageUtil;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberUpdateService {

    private final MemberRepository memberRepository;
    private final ImageUtil imageUtil;
    private final S3FileService s3FileService;

    public SuccessResponse<NicknameDto> updateNickname(NicknameDto nicknameDto, Member member) {
        String nickname = nicknameDto.getNickname();
        if (!isNicknameValidFormat(nickname)) {
            throw new InvalidNicknameException(nickname, ErrorCode.INVALID_NICKNAME_RULE);
        }

        if (nickname.equals(member.getNickname())) {
            throw new InvalidNicknameException(nickname, ErrorCode.DUPLICATED_NICKNAME);
        }

        member.setNickname(nickname);
        memberRepository.save(member);
        return SuccessResponse.of(SuccessCode.NICKNAME_UPDATE_SUCCESS.getMessage(), nicknameDto);
    }

    public SuccessResponse<ImageUpdateResponse> updateImage(ImageUpdateRequest request,
        Member member) {

        // 이미지 형식(파일 크기, 확장자) 유효성 검사
        imageUtil.validateImageFormat(request.getContentLength(), request.getExtension());

        // 기존 프로필 이미지 삭제
        deleteExistingImageIfExists(member);

        // 새로운 이미지 저장 및 URL 생성
        String preSignedUrl = saveNewImage(request, member);

        return SuccessResponse.of(
            SuccessCode.PROFILE_IMAGE_UPDATE_SUCCESS.getMessage(),
            ImageUpdateResponse.from(preSignedUrl)
        );
    }

    private String saveNewImage(ImageUpdateRequest request, Member member) {
        String savedFileName = String.format("%s.%s", UUID.randomUUID(), request.getExtension());
        String preSignedUrl = s3FileService.getPutPreSignedUrl(
            "profiles", savedFileName, request.getExtension(), request.getContentLength()
        );
        log.info("[PATCH /member/image] savedFileName: {}", savedFileName);

        member.setImageFileName(savedFileName);
        memberRepository.save(member);

        return preSignedUrl;
    }

    // TODO: 개발 환경, 운영 환경에 따라 S3 저장 위치 다르게 설정하기
    private void deleteExistingImageIfExists(Member member) {
        Optional.ofNullable(member.getImageFileName())
            .ifPresent((imageFileName) -> s3FileService.deleteImage("profiles", imageFileName));
    }

    private boolean isNicknameValidFormat(String nickname) {
        String regex = "^[a-zA-Z0-9가-힣]{2,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

}
