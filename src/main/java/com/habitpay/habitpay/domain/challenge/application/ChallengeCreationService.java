package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationResponse;
import com.habitpay.habitpay.domain.challenge.exception.ChallengeStartTimeInvalidException;
import com.habitpay.habitpay.domain.challenge.exception.InvalidChallengeParticipatingDaysException;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.challengescheduler.application.SchedulerTaskHelperService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.participationstat.dao.ParticipationStatRepository;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import com.habitpay.habitpay.global.config.timezone.TimeZoneConverter;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Service
@AllArgsConstructor
public class ChallengeCreationService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ParticipationStatRepository participationStatRepository;
    private final SchedulerTaskHelperService schedulerTaskHelperService;

    @Transactional
    public SuccessResponse<ChallengeCreationResponse> createChallenge(ChallengeCreationRequest challengeCreationRequest, Member member) {
        validateChallengeStartDate(challengeCreationRequest.getStartDate());
        validateChallengeParticipatingDays(challengeCreationRequest);

        Challenge challenge = Challenge.of(member, challengeCreationRequest);
        challenge.setNumberOfParticipants(challenge.getNumberOfParticipants() + 1);
        challengeRepository.save(challenge);

        // todo: ChallengeEnrollmentService에 반복되는 로직 존재 -> 메서드화?
        ChallengeEnrollment challengeEnrollment = ChallengeEnrollment.of(member, challenge);
        ParticipationStat participationStat = ParticipationStat.of(challengeEnrollment);
        challengeEnrollment.setParticipationStat(participationStat);
        challengeEnrollmentRepository.save(challengeEnrollment);
        participationStatRepository.save(participationStat);

        if (isStartDateIsToday(challengeCreationRequest.getStartDate())) {
            challenge.setStateInProgress();
            List<Challenge> challengeList = Collections.singletonList(challenge);
            schedulerTaskHelperService.createRecordsForChallenges(challengeList);
            challengeRepository.save(challenge);
        }

        return SuccessResponse.of(
                SuccessCode.CREATE_CHALLENGE_SUCCESS,
                ChallengeCreationResponse.of(member, challenge)
        );
    }

    private void validateChallengeParticipatingDays(ChallengeCreationRequest challengeCreationRequest) {
        ZonedDateTime startDate = TimeZoneConverter.convertEtcToLocalTimeZone(challengeCreationRequest.getStartDate());
        ZonedDateTime endDate = TimeZoneConverter.convertEtcToLocalTimeZone(challengeCreationRequest.getEndDate());
        int participatingDays = challengeCreationRequest.getParticipatingDays();

        EnumSet<DayOfWeek> challengeDays = EnumSet.noneOf(DayOfWeek.class);
        ZonedDateTime currentDate = startDate;
        while (!currentDate.isAfter(endDate) && challengeDays.size() < 7) {
            challengeDays.add(currentDate.getDayOfWeek());
            currentDate = currentDate.plusDays(1);
        }

        int count = 0;
        for (int bit = 0; bit <= 6; bit += 1) {
            int dayBitPosition = 6 - bit; // [0000000] 7자리 비트 사용. 가장 왼쪽 비트가 월요일.

            if ((participatingDays & (1 << dayBitPosition)) != 0) {
                DayOfWeek dayOfWeek = DayOfWeek.of(bit + 1); // (1=Monday, 7=Sunday)
                count = challengeDays.contains(dayOfWeek) ? count + 1 : count;
            }
        }

        if (count == 0) {
            throw new InvalidChallengeParticipatingDaysException();
        }
    }

    private void validateChallengeStartDate(ZonedDateTime startDate) {
        if (startDate.isBefore(ZonedDateTime.now())) {
            throw new ChallengeStartTimeInvalidException(startDate);
        }
    }

    private boolean isStartDateIsToday(ZonedDateTime startDate) {
        ZonedDateTime now = ZonedDateTime.now();

        if (startDate.isBefore(now)) {
            throw new ChallengeStartTimeInvalidException(startDate);
        }

        return startDate.toLocalDate().equals(now.toLocalDate());
    }
}
