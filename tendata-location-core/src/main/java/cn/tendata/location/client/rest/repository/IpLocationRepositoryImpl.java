package cn.tendata.location.client.rest.repository;

import cn.tendata.location.client.rest.model.IpLocationItem;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class IpLocationRepositoryImpl extends RestClientRepositoryImpl<IpLocationItem, String> implements IpLocationRepository {

    private final RestHighLevelClient client;

    @Autowired
    public IpLocationRepositoryImpl(RestHighLevelClient client) {
        super(client, IpLocationItem.getIndexName(), IpLocationItem.getTypeName());
        this.client = client;
    }
}
