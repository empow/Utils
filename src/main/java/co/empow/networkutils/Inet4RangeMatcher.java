package co.empow.networkutils;

import com.google.common.collect.*;
import com.google.common.net.InetAddresses;
import org.apache.commons.net.util.SubnetUtils;
import sun.nio.ch.Net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static co.empow.networkutils.NetUtils.ip2Long;


public class Inet4RangeMatcher {

    private final RangeSet<Long> ranges;

    private Inet4RangeMatcher(RangeSet<Long> ranges) {
        this.ranges = ranges;
    }

    public boolean inRange(String ip) {
        long numericValueUnsgined = ip2Long(ip);
        return ranges.contains(numericValueUnsgined);
    }

    public static class Builder {
        private final Collection<Range<Long>> ranges = new ArrayList<>();

        public Builder() {
        }

        public Inet4RangeMatcher build() {
            RangeSet<Long> rangeSet = ImmutableRangeSet.unionOf(ranges);

            rangeSet = compact(rangeSet);

            return new Inet4RangeMatcher(rangeSet);
        }

        private static RangeSet<Long> compact(RangeSet<Long> rangeSet) {
            ImmutableRangeSet.Builder<Long> builder = ImmutableRangeSet.builder();

            Set<Range<Long>> descendingRanges = rangeSet.asDescendingSetOfRanges();

            Iterator<Range<Long>> it = descendingRanges.iterator();

            Range<Long> previousRange = null;

            if (it.hasNext()) {
                previousRange = it.next();
            }

            while (it.hasNext()) {
                Range<Long> currentItem = it.next();

                Range<Long> gap = currentItem.gap(previousRange);

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

            ImmutableRangeSet<Long> compactedRanges = builder.build();

            return TreeRangeSet.create(compactedRanges);
        }

        public Builder addRange(String fromAddress, String toAddress) {
            long lowAddress = ip2Long(fromAddress);
            long highAddress = ip2Long(toAddress);

            Range<Long> range = Range.closed(lowAddress, highAddress);
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
