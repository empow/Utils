package co.empow.networkutils;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;

public class NetUtils {
    public static int ip2Integer(String ip) {
        InetAddress address = InetAddresses.forString(ip);
        return InetAddresses.coerceToInteger(address);
    }
}
