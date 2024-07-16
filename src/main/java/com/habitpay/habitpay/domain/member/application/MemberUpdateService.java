package com.habitpay.habitpay.domain.member.application;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.dto.NicknameDto;
import com.habitpay.habitpay.domain.model.Response;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.error.ErrorResponse;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class MemberUpdateService {

    private final MemberRepository memberRepository;
    private final S3FileService s3FileService;

    public SuccessResponse<NicknameDto> updateNickname(NicknameDto nicknameDto, Long id) {
        String nickname = nicknameDto.getNickname();
        if (isNicknameValidFormat(nickname) == false) {
            String message = ErrorResponse.INVALID_NICKNAME_RULE.getMessage();
            // TODO: 422(UNPROCESSABLE_ENTITY) 반환하기
            throw new IllegalArgumentException(message);
        }

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        member.setNickname(nickname);
        memberRepository.save(member);
        String message = Response.PROFILE_UPDATE_SUCCESS.getMessage();
        return SuccessResponse.of(message, nicknameDto);
    }

//    public SuccessResponse<MemberProfileResponse> updateProfile(MemberUpdateRequest memberUpdateRequest, Long id) {
//        String nickname = memberUpdateRequest.getNickname();
//        String imageExtension = memberUpdateRequest.getImageExtension();
//        Long contentLength = memberUpdateRequest.getContentLength();
//        log.info("[PATCH /member] nickname: {}, imageExtension: {}, contentLength: {}", nickname, imageExtension, contentLength);
//
//        // 1. 이미지 크기 제한이 넘을 경우
//        if (ImageUtil.isValidFileSize(contentLength) == false) {
//            String message = ErrorResponse.IMAGE_CONTENT_TOO_LARGE.getMessage();
//            // TODO: 413(PAYLOAD_TOO_LARGE) 반환하기
//            throw new IllegalArgumentException(message);
//        }
//
//        // 2. 이미지 확장자가 허용되지 않은 경우
//        if (ImageUtil.isValidImageExtension(imageExtension) == false) {
//            String message = ErrorResponse.UNSUPPORTED_IMAGE_EXTENSION.getMessage();
//            // TODO: 415(UNSUPPORTED_MEDIA_TYPE) 반환하기
//            throw new IllegalArgumentException(message);
//        }
//
//        // 5. 프로필 이미지가 이미 존재하고, 새롭게 업로드 하는 경우
//        s3FileService.deleteImage("profiles", member.getImageFileName());
//
//        String randomFileName = UUID.randomUUID().toString();
//        String savedFileName = String.format("%s.%s", randomFileName, imageExtension);
//        log.info("[PATCH /member] savedFileName: {}", savedFileName);
//
//        member.updateProfile(nickname, savedFileName);
//        memberRepository.save(member);
//
//        String preSignedUrl = s3FileService.getPutPreSignedUrl("profiles", savedFileName, imageExtension, contentLength);
//
//        return ResponseEntity.status(HttpStatus.OK).body(preSignedUrl);
//    }

    public boolean isNicknameValidFormat(String nickname) {
        String regex = "^[a-zA-Z0-9가-힣]{2,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);

        return matcher.matches();
    }

}
