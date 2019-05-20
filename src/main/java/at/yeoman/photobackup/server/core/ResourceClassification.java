package at.yeoman.photobackup.server.core;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceClassification {
    private static final Pattern NameWithExtensionPattern =
            Pattern.compile(".*\\.(\\w+)");
    
    private static final Pattern NonImageResourceNamePattern =
            Pattern.compile(".*\\.(mov|plist|mp4|m4v)", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern MovResourceNamePattern =
            Pattern.compile(".*\\.(mov)", Pattern.CASE_INSENSITIVE);
    
    public static Optional<String> fileType(String name) {
        Matcher matcher = NameWithExtensionPattern.matcher(name);
        if (matcher.matches()) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }
    
    public static boolean nonImageName(String name) {
        return NonImageResourceNamePattern.matcher(name).matches();
    }
    
    public static boolean movName(String name) {
        return MovResourceNamePattern.matcher(name).matches();
    }
}
