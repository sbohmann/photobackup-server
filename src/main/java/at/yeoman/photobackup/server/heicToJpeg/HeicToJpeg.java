package at.yeoman.photobackup.server.heicToJpeg;

public class HeicToJpeg {
    native static byte[] convert(byte[] heicData);
}
