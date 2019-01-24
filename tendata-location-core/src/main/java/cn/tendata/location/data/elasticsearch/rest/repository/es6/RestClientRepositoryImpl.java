/*
package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.data.elasticsearch.model.AbstractElasticsearchEntity;
import cn.tendata.location.data.elasticsearch.model.IpLocationItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.BulkByScrollTask;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import static cn.tendata.location.data.elasticsearch.IpLocationOperationException
        .ipHasMoreThanOneCidrException;

public abstract class RestClientRepositoryImpl<T extends AbstractElasticsearchEntity, ID extends Serializable> implements RestClientRepository<T, ID> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestHighLevelClient client;
    private final String index;
    private final String type;
    private final ObjectMapper objectMapper;
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    protected RestClientRepositoryImpl(RestHighLevelClient client, String index, String type, ObjectMapper objectMapper) {
        this.client = client;
        this.index = index;
        this.type = type;
        this.objectMapper = objectMapper;
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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


    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S search(SearchRequest request) throws IOException {
        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        final SearchHits hits = response.getHits();
        if(hits.getTotalHits() > 1){
            throw ipHasMoreThanOneCidrException(request.toString());
        }
        final String sourceAsString = hits.getAt(0).getSourceAsString();
        final T t = objectMapper.readValue(sourceAsString, clazz);
        return (S) t;
    }

    @Override
    public void delete(DeleteByQueryRequest deleteRequest) {
        final ActionListener<BulkByScrollResponse> listener = new ActionListener<BulkByScrollResponse>() {

            @Override
            public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                logger.info("delete request:{},  delete Response:{}", deleteRequest, bulkByScrollResponse);
            }

            @Override
            public void onFailure(Exception e) {
                logger.info("delete request:{}, but delete failed", deleteRequest, e);
            }
        };
        client.deleteByQueryAsync(deleteRequest, RequestOptions.DEFAULT, listener);
    }

    @Override

    public void refresh() throws IOException {
        RefreshRequest request = new RefreshRequest(index);
        final RefreshResponse refreshResponse = client.indices().refresh(request, RequestOptions.DEFAULT);
        logger.info("refresh request:{}, refresh response:{}", request, refreshResponse);
    }
}
*/
