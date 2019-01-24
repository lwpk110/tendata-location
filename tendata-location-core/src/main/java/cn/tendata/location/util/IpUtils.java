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
            final IPAddress address = ipAddress.assignMinPrefixForBlock();
            return address.toString();
        }).collect(Collectors.toList());
    }
}
