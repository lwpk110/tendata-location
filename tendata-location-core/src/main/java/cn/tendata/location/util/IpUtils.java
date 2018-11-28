package cn.tendata.location.util;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IpUtils {
    private IpUtils(){}
    public static List<String> rangeToCIDR(String start, String end) {
        IPAddressString startIpAddressString = new IPAddressString(start);
        IPAddressString endIpAddressString = new IPAddressString(end);
        IPAddress startIpAddress = startIpAddressString.getAddress();
        IPAddress endIpAddress = endIpAddressString.getAddress();
        IPAddressSeqRange range = startIpAddress.toSequentialRange(endIpAddress);
        IPAddress[] result = range.spanWithPrefixBlocks();
        return Arrays.stream(result).map(ipAddress -> {
            final String cidr = ipAddress.toString();
            if(!cidr.contains("/")){
                if(ipAddress.isIPv4()){
                    return cidr +"/32";
                }else if (ipAddress.isIPv6()){
                    return cidr +"/128";
                }
            }
            return cidr;
        }).collect(Collectors.toList());
    }

    public static boolean isIpv4(String ip){
        IPAddressString startIpAddressString = new IPAddressString(ip);
        return startIpAddressString.isIPv4();
    }

    public static boolean isIpv6(String ip){
        IPAddressString startIpAddressString = new IPAddressString(ip);
        return startIpAddressString.isIPv6();
    }
}
