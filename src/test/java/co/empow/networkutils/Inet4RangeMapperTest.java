package co.empow.networkutils;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class Inet4RangeMapperTest {

    @Test
    public void testEmptyBuilder() {
        Inet4RangeMapper<Integer> mapper = new Inet4RangeMapper.Builder().build();
        assertThat(mapper.map("1.1.1.1"), is(emptyIterable()));
    }

    @Test
    public void testOverlappingRanges() {
        Inet4RangeMapper<Integer> mapper = new Inet4RangeMapper.Builder()
                .addRange("0.0.0.1", "0.0.0.5", 1)
                .addRange("0.0.0.1", "0.0.0.5", 2)
                .addRange("0.0.0.5", "0.0.0.10", 1)
                .addRange("0.0.0.8", "0.0.0.15", 2)
                .addRange("0.0.0.8", "0.0.0.15", 1)
                .addRange("210.0.0.1", "211.0.0.1", 1)
                .addRange("0.1.0.1", "190.0.0.1", 3)
                .build();

        assertThat(mapper.map("0.0.0.6"), not(contains(2)));
        assertThat(mapper.map("0.0.0.6"), contains(1));
        assertThat(mapper.map("210.1.0.6"), contains(1));
        assertThat(mapper.map("5.0.0.6"), contains(3));

        int counter = 0;

        long st = System.currentTimeMillis();

        for (int i = 0; i < 1_000_000; i++) {
            Set<Integer> res = mapper.map("0.0.0.6");
            if (res != null) {
                ++counter;
            }
        }

        long et = System.currentTimeMillis();

        System.out.println(et-st);

        System.out.println(counter);
    }
}
