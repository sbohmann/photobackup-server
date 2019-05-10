package at.yeoman.photobackup.server.core;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;

public class IsoStringForInstantTest {
    @Test
    public void simpleCases() {
        String exampleString = "20190228T123456.789";
        Instant exampleInstant = LocalDateTime.of(
                2019, 2, 28, 12, 34, 56, 789_000_000)
                .toInstant(ZoneOffset.UTC);
        assertEquals(exampleString, IsoStringForInstant.create(exampleInstant));
        assertEquals(new InstantForIsoString(exampleString).result, exampleInstant);
        assertEquals(IsoStringForInstant.create(new InstantForIsoString(exampleString).result),
                IsoStringForInstant.create(exampleInstant));
    }
}