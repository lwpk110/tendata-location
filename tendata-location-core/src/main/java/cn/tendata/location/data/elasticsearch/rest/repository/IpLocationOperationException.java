package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.core.BasicErrorCodeException;

public class IpLocationOperationException extends BasicErrorCodeException {

    private static final String IP_HAS_MORE_THAN_ONE_CIDR = "IP_HAS_MORE_THAN_ONE_CIDR";

    public IpLocationOperationException(String errorCode, String message) {
        super(errorCode, message);
    }


    public static IpLocationOperationException ipHasMoreThanOneCidrException(String request) {
        String message = String.format("request:[%s] has more than one CIDR,only one is allowed", request);
        return new IpLocationOperationException(IP_HAS_MORE_THAN_ONE_CIDR, message);
    }
}
