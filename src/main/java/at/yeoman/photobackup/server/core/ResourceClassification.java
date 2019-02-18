package at.yeoman.photobackup.server.core;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceClassification {
    public static final Pattern NameWithExtensionPattern =
            Pattern.compile(".*\\.(\\w+)");

    public static final Pattern NonImageResourceNamePattern =
            Pattern.compile(".*\\.(mov|plist|mp4)", Pattern.CASE_INSENSITIVE);

    public static boolean nonImageName(String name) {
        return NonImageResourceNamePattern.matcher(name).matches();
    }

    public static Optional<String> fileType(String name) {
        Matcher matcher = NameWithExtensionPattern.matcher(name);
        if (matcher.matches()) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }
}
