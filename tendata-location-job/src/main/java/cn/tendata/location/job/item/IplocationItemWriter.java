package cn.tendata.location.job.item;

import cn.tendata.location.data.elasticsearch.model.IpLocationItem;
import cn.tendata.location.data.elasticsearch.IpLocationRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

import java.util.List;

public class IplocationItemWriter implements ItemWriter<IpLocationItem> {

    private final IpLocationRepository ipLocationRepository;

    public IplocationItemWriter(IpLocationRepository ipLocationRepository) {
        this.ipLocationRepository = ipLocationRepository;
    }

    @Override
    public void write(@NonNull List<? extends IpLocationItem> items) throws Exception {
        ipLocationRepository.bulkIndex(items);
    }
}
