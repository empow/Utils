package co.empow.networkutils;

import com.google.common.collect.*;
import com.google.common.net.InetAddresses;
import org.apache.commons.net.util.SubnetUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static co.empow.networkutils.NetUtils.ip2Integer;

public class Inet4RangeMatcher {

    private final RangeSet<Integer> ranges;

    private Inet4RangeMatcher(RangeSet<Integer> ranges) {
        this.ranges = ranges;
    }

    public boolean inRange(String ip) {
        InetAddress inetAddress = InetAddresses.forString(ip);
        return inRange(inetAddress);
    }

    public boolean inRange(InetAddress ip) {

        int numericValue = InetAddresses.coerceToInteger(ip);

        return ranges.contains(numericValue);
    }

    public static class Builder {
        private final Collection<Range<Integer>> ranges = new ArrayList<>();

        public Builder() {
        }

        public Inet4RangeMatcher build() {
            RangeSet<Integer> rangeSet = ImmutableRangeSet.unionOf(ranges);

            rangeSet = compact(rangeSet);

            return new Inet4RangeMatcher(rangeSet);
        }

        private static RangeSet<Integer> compact(RangeSet<Integer> rangeSet) {
            ImmutableRangeSet.Builder<Integer> builder = ImmutableRangeSet.builder();

            Set<Range<Integer>> descendingRanges = rangeSet.asDescendingSetOfRanges();

            Iterator<Range<Integer>> it = descendingRanges.iterator();

            Range<Integer> previousRange = null;

            if (it.hasNext()) {
                previousRange = it.next();
            }

            while (it.hasNext()) {
                Range<Integer> currentItem = it.next();

                Range<Integer> gap = currentItem.gap(previousRange);

                if (currentItem.isConnected(previousRange) ||
                        gap.lowerEndpoint() + 1 == gap.upperEndpoint()) {
                    previousRange = previousRange.span(currentItem);
                }
                else {
                    builder.add(previousRange);
                    previousRange = currentItem;
                }
            }
            
            if (previousRange != null) {
                builder.add(previousRange);
            }

            ImmutableRangeSet<Integer> compactedRanges = builder.build();

            return TreeRangeSet.create(compactedRanges);
        }

        public Builder addRange(String fromAddress, String toAddress) {
            int lowAddress = ip2Integer(fromAddress);
            int highAddress = ip2Integer(toAddress);

            Range<Integer> range = Range.closed(lowAddress, highAddress);
            ranges.add(range);

            return this;
        }

        public Builder addSubnet(String subnet) {
            SubnetUtils.SubnetInfo info = (new SubnetUtils(subnet)).getInfo();

            String lowAddress = info.getLowAddress();
            String highAddress = info.getHighAddress();

            return addRange(lowAddress, highAddress);
        }
    }
}
