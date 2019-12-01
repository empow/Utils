package co.empow.networkutils;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;

public class NetUtils {
    public static long ip2Long(String ip) {
        InetAddress address = InetAddresses.forString(ip);
        int ipAsInt = InetAddresses.coerceToInteger(address);
        long ipAsLong = int2long(ipAsInt);
        return ipAsLong;
    }

    private static long int2long(int i) {
        return i & 0xffffffffL;
    }
}
