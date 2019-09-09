package co.empow.networkutils;

import com.google.common.collect.*;
import com.google.common.net.InetAddresses;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static co.empow.networkutils.NetUtils.ip2Integer;

public class Inet4RangeResolver<V> {

    public final RangeMap<Integer, Set<V>> rangeMap;

    public Inet4RangeResolver(RangeMap<Integer, Set<V>> rangeMap) {
        this.rangeMap = rangeMap;
    }

    public Set<V> resolve(String ip) {
        return resolve(ip2Integer(ip));
    }

    public Set<V> resolve(InetAddress ip) {
        int numericValue = InetAddresses.coerceToInteger(ip);
        return resolve(numericValue);
    }

    private Set<V> resolve(int ip) {
        return rangeMap.getEntry(ip).getValue();
    }

    public static class Builder<V> {
        private RangeMap<Integer, Set<V>> rangeMap = TreeRangeMap.create();

        public Builder() {
        }

        public Builder<V> addRange(Range<Integer> range, V val) {
            RangeMap<Integer, Set<V>> subRange = ImmutableRangeMap.copyOf(rangeMap.subRangeMap(range));

            rangeMap.put(range, Sets.newHashSet(val));

            if (!subRange.asMapOfRanges().isEmpty()) {
                for (Map.Entry<Range<Integer>, Set<V>> entry : subRange.asMapOfRanges().entrySet()) {
                    Range<Integer> key = entry.getKey();
                    Set<V> values = entry.getValue();

                    values.add(val);
                    rangeMap.put(key, values);
                }
            }

            return this;
        }

        public Inet4RangeResolver<V> build() {
            return new Inet4RangeResolver<>(getCoalescedRanges());
        }

        private RangeMap<Integer, Set<V>> getCoalescedRanges() {
            ImmutableRangeMap.Builder<Integer, Set<V>> builder = ImmutableRangeMap.builder();

            Iterator<Map.Entry<Range<Integer>, Set<V>>> it = rangeMap.asDescendingMapOfRanges().entrySet().iterator();

            if (!it.hasNext()) {
                return builder.build();
            }

            Map.Entry<Range<Integer>, Set<V>> current = it.next();

            Range<Integer> previousRange = current.getKey();
            Set<V> previousValues = current.getValue();

            while (it.hasNext()) {
                current = it.next();
                Range<Integer> currentRange = current.getKey();
                Set<V> currentValues = current.getValue();

                if (previousValues.equals(currentValues)) {
                    Range<Integer> gap = currentRange.gap(previousRange);

                    if (currentRange.isConnected(previousRange) ||
                            gap.lowerEndpoint() + 1 == gap.upperEndpoint()) {
                        previousRange = previousRange.span(currentRange);
                    }
                    else {
                        builder.put(previousRange, previousValues);
                        previousRange = currentRange;
                        previousValues = currentValues;
                    }
                }
                else {
                    builder.put(previousRange, previousValues);
                    previousRange = currentRange;
                    previousValues = currentValues;
                }
            }

            if (previousRange != null) {
                builder.put(previousRange, previousValues);
            }

            return builder.build();
        }
    }
}
