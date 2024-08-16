package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.dao.ChallengeRepository;
import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationRequest;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeCreationResponse;
import com.habitpay.habitpay.domain.challenge.exception.ChallengeStartTimeInvalidException;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.challengeenrollment.domain.ChallengeEnrollment;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.participationstat.dao.ParticipationStatRepository;
import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
public class ChallengeCreationService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;
    private final ParticipationStatRepository participationStatRepository;

    @Transactional
    public SuccessResponse<ChallengeCreationResponse> createChallenge(ChallengeCreationRequest challengeCreationRequest, Member member) {
        if (isStartDateBeforeNow(challengeCreationRequest.getStartDate())) {
            throw new ChallengeStartTimeInvalidException(challengeCreationRequest.getStartDate());
        }

        Challenge challenge = Challenge.of(member, challengeCreationRequest);
        challenge.setNumberOfParticipants(challenge.getNumberOfParticipants() + 1);
        challengeRepository.save(challenge);

        // todo: ChallengeEnrollmentService에 반복되는 로직 존재 -> 메서드화?
        ChallengeEnrollment challengeEnrollment = ChallengeEnrollment.of(member, challenge);
        ParticipationStat participationStat = ParticipationStat.of(challengeEnrollment);
        challengeEnrollment.setParticipationStat(participationStat);
        challengeEnrollmentRepository.save(challengeEnrollment);
        participationStatRepository.save(participationStat);

        return SuccessResponse.of(
                SuccessCode.CREATE_CHALLENGE_SUCCESS,
                ChallengeCreationResponse.of(member, challenge)
        );
    }

    private boolean isStartDateBeforeNow(ZonedDateTime startDate) {
        return startDate.isBefore(ZonedDateTime.now());
    }
}
