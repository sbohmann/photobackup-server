package at.yeoman.photobackup.server.io;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class ByteArrayUtilities {
    static final Random random = new Random();
    static boolean Verbose = false;
    
    static void prettyPrint(byte[] data) {
        System.out.println(listView(data).stream()
                .map(value -> String.format("%02x", value))
                .collect(Collectors.joining(", ", "[", "]")));
    }
    
    static List<Byte> listView(byte[] data) {
        return new AbstractList<Byte>() {
            @Override
            public int size() {
                return data.length;
            }
            
            @Override
            public Byte get(int index) {
                return data[index];
            }
        };
    }
    
    static byte[] randomData(int length) {
        byte[] result = new byte[length];
        random.nextBytes(result);
        return result;
    }
    
    static byte[] truncate(byte[] original, int offset, int length) {
        int from = Math.min(offset, original.length);
        int to = Math.max(
                Math.min(offset + length, original.length),
                0);
        return Arrays.copyOfRange(original, from, to);
    }
    
    static void report(byte[] original, byte[] copy) {
        if (Verbose) {
            prettyPrint(original);
            prettyPrint(copy);
        }
    }
}
