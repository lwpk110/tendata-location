package cn.tendata.location.util;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IpUtils {
    private IpUtils() {
    }

    public static List<String> rangeToCIDR(String start, String end) {
        IPAddressString startIpAddressString = new IPAddressString(start);
        IPAddressString endIpAddressString = new IPAddressString(end);
        IPAddress startIpAddress = startIpAddressString.getAddress();
        IPAddress endIpAddress = endIpAddressString.getAddress();
        IPAddressSeqRange range = startIpAddress.toSequentialRange(endIpAddress);
        IPAddress[] result = range.spanWithPrefixBlocks();
        return Arrays.stream(result).map(ipAddress -> {
            final String cidr = ipAddress.toString();
            return ignoreSingleIp(cidr, ipAddress);
        }).collect(Collectors.toList());
    }

    private static String ignoreSingleIp(String cidr, IPAddress ipAddress) {
        if (!cidr.contains("/")) {
            if (ipAddress.isIPv4()) {
                return cidr + "/32";
            } else if (ipAddress.isIPv6()) {
                return cidr + "/128";
            }
        }
        return cidr;
    }
}
