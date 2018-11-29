package cn.tendata.location.task.batch.item;

import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem;
import cn.tendata.location.data.elasticsearch.rest.model.IpLocationItem.IpLocationDocumentBuilder;
import cn.tendata.location.util.IpUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DbipItemProcessor implements ItemProcessor<FieldSet,List<IpLocationItem>> {
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

        List<String> cidrs = IpUtils.rangeToCIDR(ipStart, ipEnd);

        List<IpLocationItem> ipLocationItems = new ArrayList<>(cidrs.size());
        for (String cidr : cidrs) {
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
        return ipLocationItems;
    }
}
