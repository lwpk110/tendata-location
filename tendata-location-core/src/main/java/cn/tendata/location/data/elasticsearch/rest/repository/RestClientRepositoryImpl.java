package cn.tendata.location.data.elasticsearch.rest.repository;

import cn.tendata.location.data.elasticsearch.model.AbstractElasticsearchEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.tendata.location.data.elasticsearch.IpLocationOperationException
        .ipHasMoreThanOneCidrException;

@Slf4j
public class RestClientRepositoryImpl<T extends AbstractElasticsearchEntity, ID extends Serializable> implements
        RestClientRepository<T, ID> {

    private final RestHighLevelClient client;
    private final String index;
    private final String type;
    private final ObjectMapper objectMapper;
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    protected RestClientRepositoryImpl(RestHighLevelClient client, String index, String type, ObjectMapper
            objectMapper) {
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
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        for (T item : items) {
            final Map map = mapper.convertValue(item, Map.class);
            request.add(new IndexRequest(index, type).source(map));
        }
        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {
                log.debug("===> bulk index response:{}", bulkItemResponses);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("===> bulk index occur a exception:{}", e.getMessage(), e);
            }
        };
        client.bulkAsync(request, listener);
    }

    @Override
    public <S extends T> S search(SearchRequest request) throws IOException {
        final SearchResponse response = client.search(request);
        final SearchHits hits = response.getHits();
        if (hits.getTotalHits() > 1) {
            throw ipHasMoreThanOneCidrException(request.toString());
        }
        final String sourceAsString = hits.getAt(0).getSourceAsString();
        final T t = objectMapper.readValue(sourceAsString, clazz);
        return (S) t;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> Collection<S> searchCollection(SearchRequest request) throws IOException {
        final SearchResponse response = client.search(request);
        final SearchHits hits = response.getHits();
        final SearchHit[] searchHits = hits.getHits();
        List<S> list = new ArrayList<>(searchHits.length);
        for (SearchHit searchHit : searchHits) {
            final T convert = convert(searchHit);
            if (null != convert) {
                S location = (S) convert;
                list.add(location);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private <S extends T> S convert(SearchHit searchHitFields) {
        final String sourceAsString = searchHitFields.getSourceAsString();
        try {
            return (S) objectMapper.readValue(sourceAsString, clazz);
        } catch (IOException e) {
            log.error("es to object err:", e);
        }
        return null;
    }

    @Override
    public void delete(DeleteRequest deleteRequest) throws IOException {
        final ActionListener<DeleteResponse> listener = new ActionListener<DeleteResponse>() {
            @Override
            public void onResponse(DeleteResponse deleteResponse) {
                log.debug("delete request:{},  delete Response:{}", deleteRequest, deleteResponse);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("delete request:{}, but delete failed", deleteRequest, e);
            }
        };
        client.deleteAsync(deleteRequest, listener);
    }

    @Override
    public void deleteById(ID id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id.toString());
        client.delete(deleteRequest);
    }
}
