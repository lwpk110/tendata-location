package cn.tendata.location.task.batch.item;

import cn.tendata.location.client.rest.model.IpLocationItem;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemWriter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SplitItemWriter implements ItemWriter<List<IpLocationItem>> {
    private final ItemWriter<IpLocationItem> delegates;

    public SplitItemWriter(ItemWriter<IpLocationItem> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void write(@Nonnull List<? extends List<IpLocationItem>> items) throws Exception {
        List<IpLocationItem> result = new ArrayList<>(items.size());
        for (List<IpLocationItem> ipLocationDocumentList : items) {
            result.addAll(ipLocationDocumentList);
        }
        if(CollectionUtils.isNotEmpty(result)){
            delegates.write(result);
        }
    }
}
