package at.yeoman.photobackup.server.core;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InstantForIsoString {
    private static Pattern utcStringPasttern =
            Pattern.compile("(\\d{4})(\\d{2})(\\d{2})T(\\d{2})(\\d{2})(\\d{2})\\.(\\d{3})");
    private static Pattern decoratedUtcStringPasttern =
            Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})");
    
    private Matcher matcher;
    
    public Instant result;
    
    InstantForIsoString(String utcString) {
        matcher = match(utcString);
        int yesr = parseGroup(1);
        int month = parseGroup(2);
        int day = parseGroup(3);
        int hour = parseGroup(4);
        int minute = parseGroup(5);
        int second = parseGroup(6);
        int millisecond = parseGroup(7);
        result = LocalDateTime.of(yesr, month, day, hour, minute, second, millisecond * 1_000_000)
                .toInstant(ZoneOffset.UTC);
    }
    
    private Matcher match(String utcString) {
        Matcher result = utcStringPasttern.matcher(utcString);
        if (result.matches()) {
            return result;
        } else {
            return matchDecorated(utcString);
        }
    }
    
    private Matcher matchDecorated(String utcString) {
        Matcher result = decoratedUtcStringPasttern.matcher(utcString);
        if (!result.matches()) {
            throw new IllegalArgumentException("Could not parse UTC string [" + utcString + "]");
        }
        return result;
    }
    
    private int parseGroup(int group) {
        return Integer.parseInt(matcher.group(group));
    }
}
