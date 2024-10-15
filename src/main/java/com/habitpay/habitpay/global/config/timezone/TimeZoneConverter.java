package com.habitpay.habitpay.global.config.timezone;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class TimeZoneConverter {

    private final TimeZoneProperties timeZoneProperties;

    public ZonedDateTime convertEtcToLocalTimeZone(ZonedDateTime etcTime) {
        return etcTime.withZoneSameInstant(ZoneId.of(timeZoneProperties.getTimeZone()));
    }
}
