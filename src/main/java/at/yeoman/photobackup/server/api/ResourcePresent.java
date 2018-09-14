package at.yeoman.photobackup.server.api;

public class ResourcePresent {
    public static final String Yes = "6c34e454bfb6ced8";
    public static final String No = "367011a20b690e6a";

    private final boolean isPresent;
    private final String confirmation;
    private final Checksum checksum;
    private final int trivialChecksum;

    public ResourcePresent(boolean isPresent, String confirmation, Checksum checksum, int trivialChecksum) {
        this.isPresent = isPresent;
        this.confirmation = confirmation;
        this.checksum = checksum;
        this.trivialChecksum = trivialChecksum;
    }

    public static String getYes() {
        return Yes;
    }

    public static String getNo() {
        return No;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public int getTrivialChecksum() {
        return trivialChecksum;
    }

    @Override
    public String toString() {
        return "ResourcePresent{" +
                "isPresent=" + isPresent +
                ", confirmation='" + confirmation + '\'' +
                ", checksum=" + checksum +
                ", trivialChecksum=" + trivialChecksum +
                '}';
    }
}
