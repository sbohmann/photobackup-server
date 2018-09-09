package at.yeoman.photobackup.server;

public class Qumquat {
    private final int nim;
    private final String text;

    Qumquat(int nim, String text) {
        this.nim = nim;
        this.text = text;
    }

    public int getNim() {
        return nim;
    }

    public String getText() {
        return text;
    }
}
