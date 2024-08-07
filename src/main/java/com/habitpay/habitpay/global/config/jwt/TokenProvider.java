package com.habitpay.habitpay.global.config.jwt;

import com.habitpay.habitpay.domain.member.domain.Member;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
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
        Long memberId = member.getId();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(String.valueOf(memberId))
                .claim("nickname", String.valueOf(member.getNickname()))
                .claim("isActive", String.valueOf(member.isActive()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    private String makeRefreshToken(Date expiry, Member member) {
        Date now = new Date();
        Long memberId = member.getId();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(String.valueOf(memberId))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료되었습니다.");
            return false;
        } catch (MalformedJwtException e) {
            log.error("잘못된 토큰 형식입니다.");
            return false;
        } catch (SignatureException e) {
            log.error("서명 검증에 실패했습니다.");
            return false;
        } catch (JwtException e) {
            log.error("JWT 처리 중 예외 발생 : {}", e.getLocalizedMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT 처리 중 JWT 이외 예외 발생 : {}", e.getLocalizedMessage());
            return false;
        }
    }
}
