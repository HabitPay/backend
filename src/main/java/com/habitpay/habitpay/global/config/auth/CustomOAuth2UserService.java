package com.habitpay.habitpay.global.config.auth;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.domain.member.domain.Role;
import com.habitpay.habitpay.global.config.auth.dto.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        String email = attributes.getEmail();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member;
        if (optionalMember.isEmpty()) {
            member = createMember(email);
            log.info("loadUser: 회원 생성 완료 {}", member.getEmail());
        } else {
            member = optionalMember.get();
            log.info("loadUser: 기존 회원 조회 성공 {}", member.getEmail());
        }

        return new CustomUserDetails(
                member, oAuth2User.getAttributes()
        );
    }

    private Member createMember(String email) {
        Member member = Member.builder()
                .email(email)
                .role(Role.USER)
                .build();
        return memberRepository.save(member);
    }
}
