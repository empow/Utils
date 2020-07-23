package co.empow.networkutils;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

public class NetUtils {

    private static final Pattern ipV4Pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static long ip2Long(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            int ipAsInt = ByteBuffer.wrap(inetAddress.getAddress()).getInt();
            return int2long(ipAsInt);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static long int2long(int i) {
        return i & 0xffffffffL;
    }

    public static boolean isIpV4(String ip) {
        return ipV4Pattern.matcher(ip).matches();
    }
}
