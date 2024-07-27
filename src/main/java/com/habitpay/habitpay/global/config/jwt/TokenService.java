package com.habitpay.habitpay.global.config.jwt;

import com.habitpay.habitpay.domain.member.application.MemberSearchService;
import com.habitpay.habitpay.domain.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenService {
//    public final static Duration ACCESS_TOKEN_EXPIRED_AT = Duration.ofHours(2);
//    public final static Duration REFRESH_TOKEN_EXPIRED_AT = Duration.ofDays(14);

    public final static Duration ACCESS_TOKEN_EXPIRED_AT = Duration.ofSeconds(30);
    public final static Duration REFRESH_TOKEN_EXPIRED_AT = Duration.ofMinutes(1);
    private final JwtProperties jwtProperties;

    private final TokenProvider tokenProvider;
    private final MemberSearchService memberSearchService;
    private final UserDetailsService userDetailsService;

    public String createAccessToken(Long id) {
        Member member = memberSearchService.getMemberById(id);

        return tokenProvider.generateToken(member, ACCESS_TOKEN_EXPIRED_AT);
    }

    public String createRefreshTokenContent(Long id) {
        Member member = memberSearchService.getMemberById(id);

        return tokenProvider.generateRefreshToken(member, REFRESH_TOKEN_EXPIRED_AT);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_GUEST")
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                token,
                authorities);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public Long getAccessTokenExpiresInToMillis() {
        return ACCESS_TOKEN_EXPIRED_AT.toMillis();
    }

    public Long getRefreshTokenExpiresInToMillis() {
        return REFRESH_TOKEN_EXPIRED_AT.toMillis();
    }
}
