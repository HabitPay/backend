package com.habitpay.habitpay.domain.refreshtoken.application;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenUtilService {

    public String getClientIpAddress() {

        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            log.info("IP : 0.0.0.0");
            return "0.0.0.0";
        }

        final String[] IpHeaderCandidates = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (String header : IpHeaderCandidates) {
            String requestIp = request.getHeader(header);
            if (Objects.nonNull(requestIp) && !requestIp.isEmpty() && !"unknown".equalsIgnoreCase(requestIp)) {
                log.info("[IP address] {}", requestIp.split(",")[0]);
                return requestIp.split(",")[0];
            }
        }
        String requestIp = request.getRemoteAddr();
        log.info("[IP address] {}", requestIp);
        return requestIp;
    }
}
