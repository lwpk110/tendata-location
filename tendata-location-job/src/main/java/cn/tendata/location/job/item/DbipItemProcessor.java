package cn.tendata.location.job.item;

import cn.tendata.location.data.elasticsearch.model.IpLocationItem;
import cn.tendata.location.data.elasticsearch.model.IpLocationItem.IpLocationDocumentBuilder;
import cn.tendata.location.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.tendata.location.data.elasticsearch.model.IpLocationItem.RANGE_FIELD_GTE;
import static cn.tendata.location.data.elasticsearch.model.IpLocationItem.RANGE_FIELD_LTE;

@Slf4j
public class DbipItemProcessor implements ItemProcessor<FieldSet, List<IpLocationItem>> {
    @Override
    public List<IpLocationItem> process(@Nonnull FieldSet item) {
        final String ipStart = item.readString(0);
        final String ipEnd = item.readString(1);
        final String continent = item.readString(2);
        final String alpha2CountryCode = item.readString(3);
        final String province = item.readString(4);
        final String city = item.readString(5);
        final double latitude = item.readDouble(6);
        final double longitude = item.readDouble(7);
        List<IpLocationItem> ipLocationItems = new ArrayList<>();
        setCidrForEsV5(ipLocationItems, ipStart, ipEnd, alpha2CountryCode, continent, province, city, latitude, longitude);
        return ipLocationItems;
    }

    private void setCidrForEsV6( List<IpLocationItem> ipLocationItems,String ipStart, String ipEnd, String alpha2CountryCode,
                                                String continent, String province, String city, double latitude,
                                                double longitude) {
        List<String> cidrs = IpUtils.rangeToCIDR(ipStart, ipEnd);
        for (String cidr : cidrs) {
            if (StringUtils.isNotBlank(cidr)) {
                final IpLocationDocumentBuilder builder = new IpLocationDocumentBuilder()  //防止内存指针为同一个
                        .ipStartInt(ipStart)
                        .ipEndInt(ipEnd)
                        .alpha2CountryCode(alpha2CountryCode)
                        .continent(continent)
                        .province(province)
                        .city(city)
                        .geoPoint(latitude, longitude);
                IpLocationItem ipLocationDocument = builder.ipCidr(cidr).build();
                ipLocationItems.add(ipLocationDocument);
            }
        }
    }

    private void setCidrForEsV5( List<IpLocationItem> ipLocationItems,String ipStart, String ipEnd, String alpha2CountryCode,
                                 String continent, String province, String city, double latitude,
                                 double longitude) {
        final IpLocationDocumentBuilder builder = new IpLocationDocumentBuilder()  //防止内存指针为同一个
                .ipStartInt(ipStart)
                .ipEndInt(ipEnd)
                .alpha2CountryCode(alpha2CountryCode)
                .continent(continent)
                .province(province)
                .city(city)
                .geoPoint(latitude, longitude);
        Map<String,String> ipRange = new HashMap<>(2);
        ipRange.put(RANGE_FIELD_GTE, ipStart);
        ipRange.put(RANGE_FIELD_LTE, ipEnd);
        IpLocationItem ipLocationDocument = builder.ipCidr(ipRange).build();
        ipLocationItems.add(ipLocationDocument);
    }
}
