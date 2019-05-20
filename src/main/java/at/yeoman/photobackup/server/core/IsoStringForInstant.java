package at.yeoman.photobackup.server.core;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class IsoStringForInstant {
    static String create(Instant instant) {
        LocalDateTime dateTime = instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        int millisecond = dateTime.getNano() / 1_000_000;
        return String.format("%04d%02d%02dT%02d%02d%02d.%03d",
                year, month, day,
                hour, minute, second,
                millisecond);
    }
    
    static String now() {
        return create(Instant.now());
    }
}
