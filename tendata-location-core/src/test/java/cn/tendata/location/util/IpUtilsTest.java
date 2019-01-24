package cn.tendata.location.util;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

public class IpUtilsTest {

    @Test
    public void test_ipv6RangeToCIDR() {
        final List<String> cidrs = IpUtils.rangeToCIDR("2c0f:ffe9::", "2c0f:ffef:ffff:ffff:ffff:ffff:ffff:ffff");
        Assertions.assertThat(3).isEqualTo(cidrs.size());
        Assertions.assertThat("[2c0f:ffe9::/32, 2c0f:ffea::/31, 2c0f:ffec::/30]").isEqualTo(cidrs.toString());
    }

    @Test
    public void test_Ipv6StartIpEqualsEndIP() {
        final List<String> cidrs = IpUtils.rangeToCIDR("2c0f:ffe9::", "2c0f:ffe9::");
        Assertions.assertThat("[2c0f:ffe9::/128]").isEqualTo(cidrs.toString());
    }

    @Test
    public void test_startIpEqualsEndIP() {
        final List<String> cidrs = IpUtils.rangeToCIDR("77.220.115.0", "77.220.115.0");
        Assertions.assertThat("[77.220.115.0/32]").isEqualTo(cidrs.toString());
    }

    @Test
    public void test_CIDRContainsSingleIP() {
        //66.43.71.1,66.43.71.255
        //77.220.115.0,77.220.116.64
        final List<String> cidrs = IpUtils.rangeToCIDR("77.220.115.0", "77.220.116.64");
        Assertions.assertThat("[77.220.115.0/24, 77.220.116.0/26, 77.220.116.64/32]").isEqualTo(cidrs.toString());
    }
}