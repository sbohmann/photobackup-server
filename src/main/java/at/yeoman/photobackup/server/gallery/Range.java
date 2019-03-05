package at.yeoman.photobackup.server.gallery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Range {
    private static final Pattern RangePattern = Pattern.compile("bytes=(\\d+)-(\\d+)");

    final long first;
    final long last;

    Range(long first, long last) {
        this.first = first;
        this.last = last;
    }

    static Range parse(String rangeHeader) {
        if (rangeHeader != null) {
            Matcher matcher = RangePattern.matcher(rangeHeader);
            if (matcher.matches()) {
                return new Range(
                        Long.parseLong(matcher.group(1)),
                        Long.parseLong(matcher.group(2)));
            }
        }
        return null;
    }
}
