package at.yeoman.photobackup.server.gallery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class YearAndMonth {
    private static final Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})");

    boolean valid;
    int year;
    int month;

    YearAndMonth(String textualRepresentation) {
        Matcher matcher = pattern.matcher(textualRepresentation);
        if (matcher.matches()) {
            valid = true;
            year = Integer.parseInt(matcher.group(1));
            month = Integer.parseInt(matcher.group(2));
        }
    }
}
