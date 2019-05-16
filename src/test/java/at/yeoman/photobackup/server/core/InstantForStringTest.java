package at.yeoman.photobackup.server.core;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class InstantForStringTest {
    @Test
    public void simpleExamples() {
        assertEquals(Instant.parse("1970-01-01T00:00:00Z"),
                new InstantForIsoString("19700101T000000.000").result);
        assertEquals(Instant.parse("2019-02-10T12:34:56.789Z"),
                new InstantForIsoString("20190210T123456.789").result);
    }
}