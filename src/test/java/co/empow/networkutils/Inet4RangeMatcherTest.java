package co.empow.networkutils;

import org.junit.Test;

import static org.junit.Assert.*;

public class Inet4RangeMatcherTest {

    @Test
    public void testOverlappingRanges() {
        Inet4RangeMatcher matcher = new Inet4RangeMatcher.Builder()
                .addRange("1.1.1.1", "1.1.1.5")
                .addRange("1.1.1.5", "1.1.1.10")
                .addRange("1.1.1.8", "1.1.1.15")
                .build();

        assertTrue(matcher.inRange("1.1.1.6"));
    }

    @Test
    public void testAdjacentRanges() {
        Inet4RangeMatcher matcher = new Inet4RangeMatcher.Builder()
                .addRange("1.1.1.1", "1.1.1.5")
                .addRange("1.1.1.6", "1.1.1.10")
                .build();

        assertTrue(matcher.inRange("1.1.1.6"));
    }

    @Test
    public void testOutsideRange() {
        Inet4RangeMatcher matcher = new Inet4RangeMatcher.Builder()
                .addRange("1.1.1.1", "1.1.1.3")
                .addRange("1.1.1.6", "1.1.1.10")
                .build();

        assertFalse(matcher.inRange("1.1.1.4"));
    }

    @Test
    public void testUsingMask() {
        Inet4RangeMatcher matcher = new Inet4RangeMatcher.Builder()
                .addSubnet("1.1.1.1/24")
                .build();

        assertTrue(matcher.inRange("1.1.1.128"));
        assertFalse(matcher.inRange("1.1.2.1"));
    }
}
