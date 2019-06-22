package co.empow.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LUIDTest {

    @Test(expected=IllegalArgumentException.class)
    public void testConstructor1() throws Exception {
        new LUID(-1, Clock.systemDefaultZone());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructor2() throws Exception {
        new LUID(4096, Clock.systemDefaultZone());
    }

    @Test
    public void nextID() throws Exception {
        long millis = System.currentTimeMillis();
        Clock clock = mock(Clock.class);
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(millis));

        LUID luid = new LUID(0, clock);

        verifyValue(millis, 0, luid);
        verifyValue(millis, 1, luid);

        // move the clock
        ++millis;
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(millis));
        verifyValue(millis, 0, luid);
    }

    private void verifyValue(long ts, long currentSequence, LUID luid) {
        long res = luid.nextId();
        long expected = ((ts-1288834974657L) << 22) + currentSequence;

        assertEquals(expected, res);
    }

    @Test(expected = IllegalStateException.class)
    public void illegalClockReading() throws Exception {
        long millis = System.currentTimeMillis();
        Clock clock = mock(Clock.class);
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(millis));

        LUID luid = new LUID(15, clock);

        long garbage = luid.nextId();

        when(clock.instant()).thenReturn(Instant.ofEpochMilli(millis-1));

        garbage = luid.nextId();
    }

    @Test
    public void testStaticTimeFilter() throws Exception {
        long millis = System.currentTimeMillis();

        long res = LUID.getTimeFilter(millis);
        assertEquals(((millis-1288834974657L) << 22), res);
    }
}
