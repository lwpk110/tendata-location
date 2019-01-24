/*
package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.data.elasticsearch.model.IpLocationItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

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
    public void deleteByStartIpAndEndIp(String startIp, String endIp) {
        final String indexName = IpLocationItem.getIndexName();
        final QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("ipStart", startIp))
                .must(QueryBuilders.termQuery("ipEnd", endIp));
        DeleteByQueryRequest deleteRequest = new DeleteByQueryRequest(indexName);
        deleteRequest.setQuery(queryBuilder);
        this.delete(deleteRequest);
    }
}
*/
