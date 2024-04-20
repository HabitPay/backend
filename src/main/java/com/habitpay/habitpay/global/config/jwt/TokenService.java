package com.habitpay.habitpay.global.config.jwt;

import com.habitpay.habitpay.domain.member.dao.MemberRepository;
import com.habitpay.habitpay.domain.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final JwtProperties jwtProperties;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    // todo : for temp
//    private final static Duration ACCESS_TOKEN_EXPIRED_AT = Duration.ofHours(1);

    private final static Duration ACCESS_TOKEN_EXPIRED_AT = Duration.ofSeconds(30);
    private final static Duration REFRESH_TOKEN_EXPIRED_AT = Duration.ofDays(14);

    public String createAccessToken(String email) {
        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        Member member = optionalMember.get();

        return tokenProvider.generateToken(member, ACCESS_TOKEN_EXPIRED_AT);
    }

    public String createRefreshToken(String email) {
        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        Member member = optionalMember.get();

        return tokenProvider.generateRefreshToken(member, REFRESH_TOKEN_EXPIRED_AT);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_GUEST")
        );

        return new UsernamePasswordAuthenticationToken(
                new User(claims.getSubject(), "", authorities),
                token,
                authorities);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean getIsActive(String token) {
        Claims claims = getClaims(token);
        return claims.get("isActive", Boolean.class);
    }

    public Optional<String> getTokenFromHeader(String header) {
        StringTokenizer tokenizer = new StringTokenizer(header);
        if (tokenizer.countTokens() != 2 || !tokenizer.nextToken().equals("Bearer")) {
            return Optional.empty();
        }
        return Optional.of(tokenizer.nextToken());
    }

    public Long getAccessTokenExpiresInToMillis() {
        return ACCESS_TOKEN_EXPIRED_AT.toMillis();
    }

    public Long getRefreshTokenExpiresInToMillis() {
        return REFRESH_TOKEN_EXPIRED_AT.toMillis();
    }
}
