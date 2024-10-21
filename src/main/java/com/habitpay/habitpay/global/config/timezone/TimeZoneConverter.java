package com.habitpay.habitpay.global.config.timezone;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class TimeZoneConverter {

    private static TimeZoneProperties timeZoneProperties;

    @Autowired
    public TimeZoneConverter(TimeZoneProperties timeZoneProperties) {
        TimeZoneConverter.timeZoneProperties = timeZoneProperties;
    }

    public static ZonedDateTime convertEtcToLocalTimeZone(ZonedDateTime etcTime) {
        return etcTime.withZoneSameInstant(ZoneId.of(timeZoneProperties.getTimeZone()));
    }
}
