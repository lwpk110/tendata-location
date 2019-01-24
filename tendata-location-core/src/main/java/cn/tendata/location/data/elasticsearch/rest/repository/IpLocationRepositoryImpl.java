package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.data.elasticsearch.IpLocationRepository;
import cn.tendata.location.data.elasticsearch.model.IpLocationItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;

@Repository
public class IpLocationRepositoryImpl extends RestClientRepositoryImpl<IpLocationItem, String> implements
        IpLocationRepository {


    @Autowired
    public IpLocationRepositoryImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
        super(client, IpLocationItem.getIndexName(), IpLocationItem.getTypeName(), objectMapper);
    }

    @Override
    public IpLocationItem findByIp(String ip) throws IOException {
        SearchRequest searchRequest =  new SearchRequest(IpLocationItem.getIndexName());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("ipCidr",ip));
        searchRequest.source(searchSourceBuilder);
        return this.search(searchRequest);
    }

    @Override
    public void deleteByStartIpAndEndIp(String startIp, String endIp) throws IOException {
        final Collection<IpLocationItem> list = this.findByStartIpAndEndIp(startIp, endIp);
        for (IpLocationItem ipLocationItem : list) {
            String docId = ipLocationItem.getId();
            this.deleteById(docId);
        }
    }

    @Override
    public Collection<IpLocationItem> findByStartIpAndEndIp(String startIp, String endIp) throws IOException {
        SearchRequest searchRequest =  new SearchRequest(IpLocationItem.getIndexName());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("ipStart", startIp))
                .must(QueryBuilders.termQuery("ipEnd", endIp)));
        searchRequest.source(searchSourceBuilder);
        return this.searchCollection(searchRequest);
    }
}

