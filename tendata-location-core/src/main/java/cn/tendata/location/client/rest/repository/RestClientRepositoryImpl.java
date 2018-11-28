package cn.tendata.location.client.rest.repository;

import cn.tendata.location.client.rest.model.AbstractElasticsearchEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

public abstract class RestClientRepositoryImpl<T extends AbstractElasticsearchEntity, ID extends Serializable> implements RestClientRepository<T, ID> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestHighLevelClient client;
    private final String index;
    private final String type;

    protected RestClientRepositoryImpl(RestHighLevelClient client, String index, String type) {
        this.client = client;
        this.index = index;
        this.type = type;
    }


    @Override
    public <S extends T> void bulkIndex(Iterable<S> items) {
        BulkRequest request = new BulkRequest();
         ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        for (T item : items) {
            final Map map = mapper.convertValue(item, Map.class);
            request.add(new IndexRequest(index, type).source(map));
        }

        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {

            }

            @Override
            public void onFailure(Exception e) {
                logger.warn("===> bulk index occur a exception:{}",e.getMessage(), e);
            }
        };
        client.bulkAsync(request, RequestOptions.DEFAULT,listener);
    }

}
