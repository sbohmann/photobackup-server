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
            Matcher matcher = RangePattern.matcher(rangeHeader);
            if (matcher.matches()) {
                if (matcher.group(2).isEmpty()) {
                    return new Range(
                            Long.parseLong(matcher.group(1)));
                } else {
                    return new Range(
                            Long.parseLong(matcher.group(1)),
                            Long.parseLong(matcher.group(2)));
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Range{" +
                "first=" + first +
                ", last=" + last +
                ", open=" + open +
                '}';
    }
}
