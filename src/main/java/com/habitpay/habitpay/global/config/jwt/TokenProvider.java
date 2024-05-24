package com.habitpay.habitpay.global.config.jwt;

import com.habitpay.habitpay.domain.member.domain.Member;
import com.habitpay.habitpay.global.error.CustomJwtErrorInfo;
import com.habitpay.habitpay.domain.refreshToken.exception.CustomJwtException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiredAt.toMillis());
        return makeToken(expiredDate, member);
    }

    public String generateRefreshToken(Member member, Duration expiredAt) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiredAt.toMillis());
        return makeRefreshToken(expiredDate, member);
    }

    private String makeToken(Date expiry, Member member) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(member.getEmail())
                .claim("nickname", String.valueOf(member.getNickname()))
                .claim("isActive", String.valueOf(member.isActive()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    private String makeRefreshToken(Date expiry, Member member) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(member.getEmail())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // todo : error info를 주고 싶지 않으면, 그냥 return false 하고, 밖(interceptor)에서 throw exception 하기
            throw new CustomJwtException(HttpStatus.UNAUTHORIZED, CustomJwtErrorInfo.UNAUTHORIZED, e.getMessage());
        }
    }
}
