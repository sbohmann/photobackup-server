package at.yeoman.photobackup.server.configuration;

public class OperatingSystem {
    public static final boolean Windows;
    public static final boolean Linux;
    
    static {
        String osName = System.getProperty("os.name");
        System.out.println("os.name: [" + osName + "]");
        Windows = osName.startsWith("Windows");
        Linux = osName.startsWith("Linux");
    }
}
