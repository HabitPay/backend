package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeEnrolledListItemResponse;
import com.habitpay.habitpay.domain.challenge.dto.ChallengePageResponse;
import com.habitpay.habitpay.domain.challenge.exception.ChallengeNotFoundException;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengeparticipationrecord.dao.ChallengeParticipationRecordRepository;
import com.habitpay.habitpay.domain.challengeparticipationrecord.domain.ChallengeParticipationRecord;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import com.habitpay.habitpay.global.config.aws.S3FileService;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import com.habitpay.habitpay.global.response.PageResponse;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ChallengeSearchService {

    private final S3FileService s3FileService;
    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ChallengeParticipationRecordRepository challengeParticipationRecordRepository;
    private final ChallengeUtilService challengeUtilService;
    private final TimeZoneConverter timeZoneConverter;

    public SuccessResponse<PageResponse<ChallengePageResponse>> getChallengePage(Pageable pageable) {
        Page<ChallengePageResponse> challengePage = challengeRepository.findAll(pageable)
                .map(challenge -> {
                    String hostProfileImage = Optional.ofNullable(challenge.getHost().getImageFileName())
                            .map((imageFilename) -> s3FileService.getGetPreSignedUrl("profiles", imageFilename))
                            .orElse("");
                    return ChallengePageResponse.of(challenge, hostProfileImage);
                });

        return SuccessResponse.of(SuccessCode.NO_MESSAGE, PageResponse.from(challengePage));
    }

    public SuccessResponse<List<ChallengeEnrolledListItemResponse>> getEnrolledChallengeList(Member member) {
        List<ChallengeEnrollment> challengeEnrollmentList = challengeEnrollmentRepository.findAllByMember(member);
        List<ChallengeEnrolledListItemResponse> response = challengeEnrollmentList.stream()
                .map(this::mapToResponse)
                .toList();

        return SuccessResponse.of(SuccessCode.NO_MESSAGE, response);
    }

    @Transactional(readOnly = true)
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ChallengeNotFoundException(id));
    }

    private ChallengeEnrolledListItemResponse mapToResponse(ChallengeEnrollment challengeEnrollment) {
        Challenge challenge = challengeEnrollment.getChallenge();
        ParticipationStat stat = challengeEnrollment.getParticipationStat();
        ZonedDateTime startOfToday = timeZoneConverter.convertEtcToLocalTimeZone(ZonedDateTime.now()).with(LocalTime.MIDNIGHT);
        boolean isParticipatedToday = challengeParticipationRecordRepository
                .findByChallengeEnrollmentAndTargetDate(challengeEnrollment, startOfToday)
                .map(ChallengeParticipationRecord::existChallengePost)
                .orElseGet(() -> false);
        String hostProfileImageUrl = Optional.ofNullable(challenge.getHost().getImageFileName())
                .map((imageFileName) -> s3FileService.getGetPreSignedUrl("profiles", imageFileName))
                .orElse("");
        boolean isTodayParticipatingDay = challengeUtilService.isTodayParticipatingDay(challenge);
        return ChallengeEnrolledListItemResponse.of(challenge, challengeEnrollment, stat, hostProfileImageUrl, isTodayParticipatingDay, isParticipatedToday);
    }
}
