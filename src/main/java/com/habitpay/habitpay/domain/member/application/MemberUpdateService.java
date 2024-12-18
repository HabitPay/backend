package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateRequest;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateResponse;
import com.habitpay.habitpay.domain.member.dto.NicknameDto;
import com.habitpay.habitpay.domain.member.exception.InvalidNicknameException;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.error.exception.ErrorCode;
import com.habitpay.habitpay.global.error.exception.InvalidValueException;
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

    public SuccessResponse<ImageUpdateResponse> updateImage(ImageUpdateRequest imageUpdateRequest,
        Member member) {
        Long contentLength = imageUpdateRequest.getContentLength();
        String extension = imageUpdateRequest.getExtension();

        validateImageFormat(contentLength, extension);

        // 프로필 이미지가 이미 존재하는 경우 기존 이미지 삭제
        Optional.ofNullable(member.getImageFileName())
            .ifPresent((imageFileName) -> s3FileService.deleteImage("profiles", imageFileName));

        //  프론트엔드에 preSignedUrl 발급
        String randomFileName = UUID.randomUUID().toString();
        String savedFileName = String.format("%s.%s", randomFileName, extension);
        String preSignedUrl = s3FileService.getPutPreSignedUrl("profiles", savedFileName, extension,
            contentLength);
        log.info("[PATCH /member/image] savedFileName: {}", savedFileName);

        member.setImageFileName(savedFileName);
        memberRepository.save(member);

        return SuccessResponse.of(
            SuccessCode.PROFILE_IMAGE_UPDATE_SUCCESS.getMessage(),
            ImageUpdateResponse.from(preSignedUrl)
        );
    }

    private void validateImageFormat(Long contentLength, String extension) {

        // 1. 이미지 크기 제한이 넘을 경우
        if (!imageUtil.isValidFileSize(contentLength)) {
            throw new InvalidValueException(
                String.format("size: %dMB", contentLength / 1024 / 1024),
                ErrorCode.PROFILE_IMAGE_SIZE_TOO_LARGE);
        }

        // 2. 이미지 확장자가 허용되지 않은 경우
        if (!imageUtil.isValidImageExtension(extension)) {
            throw new InvalidValueException(String.format("extension: %s", extension),
                ErrorCode.UNSUPPORTED_IMAGE_EXTENSION);
        }

    }

    private boolean isNicknameValidFormat(String nickname) {
        String regex = "^[a-zA-Z0-9가-힣]{2,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

}
