package cn.tendata.location.task.batch.item;

import cn.tendata.location.client.rest.model.IpLocationItem;
import cn.tendata.location.client.rest.repository.IpLocationRepository;
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
