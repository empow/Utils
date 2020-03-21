package co.empow.networkutils;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;
import java.util.regex.Pattern;

public class NetUtils {

    private static final Pattern ipV4Pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static long ip2Long(String ip) {
        InetAddress address = InetAddresses.forString(ip);
        int ipAsInt = InetAddresses.coerceToInteger(address);
        long ipAsLong = int2long(ipAsInt);
        return ipAsLong;
    }

    private static long int2long(int i) {
        return i & 0xffffffffL;
    }

    public static boolean isIpV4(String ip) {
        return ipV4Pattern.matcher(ip).matches();
    }
}
