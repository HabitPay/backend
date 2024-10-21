package com.habitpay.habitpay.global.config.timezone;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class TimeZoneConverter {
    private static final TimeZoneProperties timeZoneProperties = new TimeZoneProperties();

    public static ZonedDateTime convertEtcToLocalTimeZone(ZonedDateTime etcTime) {
        return etcTime.withZoneSameInstant(ZoneId.of(timeZoneProperties.getTimeZone()));
    }
}
