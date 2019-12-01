package co.empow.networkutils;

import com.google.common.collect.*;
import com.google.common.net.InetAddresses;
import org.apache.commons.net.util.SubnetUtils;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static co.empow.networkutils.NetUtils.ip2Long;

public class Inet4RangeMapper<V> {

    public final RangeMap<Long, Set<V>> rangeMap;

    public Inet4RangeMapper(RangeMap<Long, Set<V>> rangeMap) {
        this.rangeMap = rangeMap;
    }

    public Set<V> map(String ip) {
        return map(ip2Long(ip));
    }

    public Set<V> map(InetAddress ip) {
        int numericValue = InetAddresses.coerceToInteger(ip);
        return map(numericValue);
    }

    private Set<V> map(long ip) {
        Map.Entry<Range<Long>, Set<V>> entry = rangeMap.getEntry(ip);

        if (entry == null) {
            return Collections.emptySet();
        }
        
        return entry.getValue();
    }

    public static class Builder<V> {
        private RangeMap<Long, Set<V>> rangeMap = TreeRangeMap.create();

        public Builder() {
        }

        public Builder addRange(String fromAddress, String toAddress, V val) {
            long lowAddress = ip2Long(fromAddress);
            long highAddress = ip2Long(toAddress);

            Range<Long> range = Range.closed(lowAddress, highAddress);

            return addRange(range, val);
        }

        public Builder addSubnet(String subnet, V val) {
            SubnetUtils.SubnetInfo info = (new SubnetUtils(subnet)).getInfo();

            String lowAddress = info.getLowAddress();
            String highAddress = info.getHighAddress();

            return addRange(lowAddress, highAddress, val);
        }

        private Builder<V> addRange(Range<Long> range, V val) {
            RangeMap<Long, Set<V>> subRange = ImmutableRangeMap.copyOf(rangeMap.subRangeMap(range));

            rangeMap.put(range, Sets.newHashSet(val));

            if (!subRange.asMapOfRanges().isEmpty()) {
                for (Map.Entry<Range<Long>, Set<V>> entry : subRange.asMapOfRanges().entrySet()) {
                    Range<Long> currentKey = entry.getKey();
                    Range<Long> intersection = currentKey.intersection(range);

                    Set<V> values = Sets.newHashSet(entry.getValue());
                    values.add(val);
                    
                    rangeMap.put(intersection, values);
                }
            }

            return this;
        }

        public Inet4RangeMapper<V> build() {
            return new Inet4RangeMapper<>(getCoalescedRanges());
        }

        private RangeMap<Long, Set<V>> getCoalescedRanges() {
            ImmutableRangeMap.Builder<Long, Set<V>> builder = ImmutableRangeMap.builder();

            Iterator<Map.Entry<Range<Long>, Set<V>>> it = rangeMap.asDescendingMapOfRanges().entrySet().iterator();

            if (!it.hasNext()) {
                return builder.build();
            }

            Map.Entry<Range<Long>, Set<V>> current = it.next();

            Range<Long> previousRange = current.getKey();
            Set<V> previousValues = current.getValue();

            while (it.hasNext()) {
                current = it.next();
                Range<Long> currentRange = current.getKey();
                Set<V> currentValues = current.getValue();

                if (previousValues.equals(currentValues)) {
                    Range<Long> gap = currentRange.gap(previousRange);

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
