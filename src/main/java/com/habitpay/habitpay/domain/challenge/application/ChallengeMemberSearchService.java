package com.habitpay.habitpay.domain.challenge.application;

import com.habitpay.habitpay.domain.challenge.domain.Challenge;
import com.habitpay.habitpay.domain.challenge.dto.ChallengeEnrolledMember;
import com.habitpay.habitpay.domain.challengeenrollment.dao.ChallengeEnrollmentRepository;
import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.response.SuccessCode;
import com.habitpay.habitpay.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeMemberSearchService {

    private final ChallengeSearchService challengeSearchService;
    private final MemberSearchService memberSearchService;
    private final ChallengeEnrollmentRepository challengeEnrollmentRepository;

    public SuccessResponse<List<ChallengeEnrolledMember>> getEnrolledMemberList(Long challengeId, Member currentUser) {
        Challenge challenge = challengeSearchService.getChallengeById(challengeId);
        List<Member> memberList = challengeEnrollmentRepository.findAllMemberByChallenge(challenge);
        List<ChallengeEnrolledMember> enrolledMemberList = makeEnrolledMemberList(memberList, currentUser);

        return SuccessResponse.of(
                SuccessCode.NO_MESSAGE,
                enrolledMemberList
        );
    }

    private List<ChallengeEnrolledMember> makeEnrolledMemberList(List<Member> memberList, Member currentUser) {
        return memberList.stream()
                .map(member -> {
                    String imageUrl = memberSearchService.getMemberProfileImageUrl(member.getImageFileName());
                    Boolean isCurrentUser = member.equals(currentUser);
                    return ChallengeEnrolledMember.of(member.getId(), member.getNickname(), imageUrl, isCurrentUser);
                })
                .collect(Collectors.toList());
    }
}
