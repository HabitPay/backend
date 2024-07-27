package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateRequest;
import com.habitpay.habitpay.domain.member.dto.ImageUpdateResponse;
import com.habitpay.habitpay.domain.member.dto.NicknameDto;
import com.habitpay.habitpay.domain.model.Response;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.response.SuccessResponse;
import com.habitpay.habitpay.global.util.ImageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class MemberUpdateService {

    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;

    private final String INVALID_NICKNAME_RULE = "닉네임 규칙에 맞지 않습니다.";
    private final String IMAGE_CONTENT_TOO_LARGE = "이미지 파일의 크기가 제한을 초과했습니다.";
    private final String UNSUPPORTED_IMAGE_EXTENSION = "지원하지 않는 이미지 확장자입니다.";

    public SuccessResponse<NicknameDto> updateNickname(NicknameDto nicknameDto, Member member) {
        String nickname = nicknameDto.getNickname();
        if (isNicknameValidFormat(nickname) == false) {
            // TODO: 422(UNPROCESSABLE_ENTITY) 반환하기
            throw new IllegalArgumentException(INVALID_NICKNAME_RULE);
        }

        member.setNickname(nickname);
        memberRepository.save(member);
        String message = Response.PROFILE_UPDATE_SUCCESS.getMessage();
        return SuccessResponse.of(message, nicknameDto);
    }

    public SuccessResponse<ImageUpdateResponse> updateImage(ImageUpdateRequest imageUpdateRequest, Long id) {
        Long contentLength = imageUpdateRequest.getContentLength();
        String extension = imageUpdateRequest.getExtension();

        // 1. 이미지 크기 제한이 넘을 경우
        if (ImageUtil.isValidFileSize(contentLength) == false) {
//            String message = ErrorResponse.IMAGE_CONTENT_TOO_LARGE.getMessage();
            // TODO: 413(PAYLOAD_TOO_LARGE) 반환하기
            throw new IllegalArgumentException(IMAGE_CONTENT_TOO_LARGE);
        }

        // 2. 이미지 확장자가 허용되지 않은 경우
        if (ImageUtil.isValidImageExtension(extension) == false) {
            // TODO: 415(UNSUPPORTED_MEDIA_TYPE) 반환하기
            throw new IllegalArgumentException(UNSUPPORTED_IMAGE_EXTENSION);
        }

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        // 3. 프로필 이미지가 이미 존재하는 경우 기존 이미지 삭제
        if (Optional.ofNullable(member.getImageFileName()).isPresent()) {
            s3FileService.deleteImage("profiles", member.getImageFileName());
        }

        // 4. 프론트엔드에 preSignedUrl 발급
        String randomFileName = UUID.randomUUID().toString();
        String savedFileName = String.format("%s.%s", randomFileName, extension);
        log.info("[PATCH /member] savedFileName: {}", savedFileName);

        member.setImageFileName(savedFileName);
        memberRepository.save(member);

        String preSignedUrl = s3FileService.getPutPreSignedUrl("profiles", savedFileName, extension, contentLength);
        String message = Response.PROFILE_UPDATE_SUCCESS.getMessage();
        return SuccessResponse.of(
                message,
                ImageUpdateResponse.from(preSignedUrl)
        );
    }

    public boolean isNicknameValidFormat(String nickname) {
        String regex = "^[a-zA-Z0-9가-힣]{2,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

}
