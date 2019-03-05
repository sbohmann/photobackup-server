package at.yeoman.photobackup.server.gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Range {
    private static final Logger log = LoggerFactory.getLogger(Range.class);

    private static final Pattern RangePattern = Pattern.compile("bytes=(\\d+)-(\\d*)");

    final long first;
    final long last;
    final boolean open;

    Range(long first, long last) {
        this.first = first;
        this.last = last;
        open = false;
    }

    Range(long first) {
        this.first = first;
        this.last = 0;
        open = true;
    }

    static Range parse(String rangeHeader) {
        if (rangeHeader != null) {
            log.info("Non-null range header: " + rangeHeader);
            Matcher matcher = RangePattern.matcher(rangeHeader);
            if (matcher.matches()) {
                if (matcher.group(2).isEmpty()) {
                    log.info("returning an open range");
                    return new Range(
                            Long.parseLong(matcher.group(1)));
                } else {
                    log.info("returning a closed range");
                    return new Range(
                            Long.parseLong(matcher.group(1)),
                            Long.parseLong(matcher.group(2)));
                }
            }
        }
        log.info("returning a null range");
        return null;
    }
}
