package com.habitpay.habitpay.global.config.timezone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class TimeZoneConverter {

    private final TimeZoneProperties timeZoneProperties;

    public ZonedDateTime convertEtcToTargetTimeZone(ZonedDateTime etcTime) {
        return etcTime.withZoneSameInstant(ZoneId.of(timeZoneProperties.getTimeZone()));
    }
}
